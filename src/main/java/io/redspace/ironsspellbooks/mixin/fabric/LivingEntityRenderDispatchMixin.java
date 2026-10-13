package io.redspace.ironsspellbooks.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link RenderLivingEvent} Pre/Post around vanilla living-entity render. */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderDispatchMixin {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$renderPre(LivingEntity entity, float yaw, float partialTick,
                                            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                            CallbackInfo ci) {
        RenderLivingEvent.Pre event = new RenderLivingEvent.Pre(entity,
                (LivingEntityRenderer) (Object) this, partialTick, poseStack, bufferSource, packedLight);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"))
    private void irons_spellbooks$renderPost(LivingEntity entity, float yaw, float partialTick,
                                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                             CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity,
                (LivingEntityRenderer) (Object) this, partialTick, poseStack, bufferSource, packedLight));
    }
}
