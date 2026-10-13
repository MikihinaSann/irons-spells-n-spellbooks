package io.redspace.ironsspellbooks.mixin.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link RenderLevelStageEvent} for the stages Fabric's WorldRenderEvents
 * does not cover: AFTER_SKY, AFTER_WEATHER, and the per-RenderType block-layer
 * stages (AFTER_TRANSLUCENT_BLOCKS etc.).
 *
 * <p>NeoForge semantic: these stages are dispatched with a {@code null} pose
 * stack — the event exposes a fresh identity {@link com.mojang.blaze3d.vertex.PoseStack}
 * — while {@link RenderLevelStageEvent#getModelViewMatrix()} carries the camera
 * rotation matrix. Subscribers bake {@code entityPos - cameraPos} into vertices
 * themselves and rely on the bound shader's model-view uniform for rotation.
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererStageMixin {
    @Shadow
    private Frustum cullingFrustum;

    private void irons_spellbooks$dispatch(RenderLevelStageEvent.Stage stage, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                           Matrix4f modelView, Matrix4f projection, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new RenderLevelStageEvent(
                stage,
                mc.levelRenderer, poseStack, modelView, projection,
                mc.level != null ? (int) mc.level.getGameTime() : 0,
                mc.getTimer(), camera, this.cullingFrustum));
    }

    private static RenderLevelStageEvent.Stage irons_spellbooks$stageFor(RenderType type) {
        if (type == RenderType.solid()) {
            return RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS;
        } else if (type == RenderType.cutoutMipped()) {
            return RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS;
        } else if (type == RenderType.cutout()) {
            return RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS;
        } else if (type == RenderType.translucent()) {
            return RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS;
        } else if (type == RenderType.tripwire()) {
            return RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS;
        }
        return null;
    }

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void irons_spellbooks$afterSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float partialTick,
                                           Camera camera, boolean isFoggy, Runnable setupFog, CallbackInfo ci) {
        // matrix4f is renderLevel's camera-rotation modelview (NeoForge passes the
        // same param); RenderSystem's modelview is still the pre-level matrix here.
        irons_spellbooks$dispatch(RenderLevelStageEvent.Stage.AFTER_SKY, null,
                matrix4f, projectionMatrix, camera);
    }

    @Inject(method = "renderSnowAndRain", at = @At("RETURN"))
    private void irons_spellbooks$afterWeather(net.minecraft.client.renderer.LightTexture lightTexture,
                                               float partialTick, double x, double y, double z, CallbackInfo ci) {
        // NeoForge hands the level pose (camera rotation) to this stage, not an identity stack.
        Matrix4f modelView = RenderSystem.getModelViewMatrix();
        com.mojang.blaze3d.vertex.PoseStack pose = new com.mojang.blaze3d.vertex.PoseStack();
        pose.last().pose().set(modelView);
        irons_spellbooks$dispatch(RenderLevelStageEvent.Stage.AFTER_WEATHER, pose,
                modelView, RenderSystem.getProjectionMatrix(),
                Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    @Inject(method = "renderSectionLayer", at = @At("RETURN"))
    private void irons_spellbooks$afterSectionLayer(RenderType renderType, double x, double y, double z,
                                                    Matrix4f modelView, Matrix4f projection, CallbackInfo ci) {
        RenderLevelStageEvent.Stage stage = irons_spellbooks$stageFor(renderType);
        if (stage != null) {
            irons_spellbooks$dispatch(stage, null, modelView, projection, Minecraft.getInstance().gameRenderer.getMainCamera());
        }
    }
}
