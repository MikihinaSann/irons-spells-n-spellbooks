package io.redspace.ironsspellbooks.mixin.fabric;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires NeoForge-style fog viewport events ({@code ComputeFogColor},
 * {@code RenderFog}) around vanilla fog computation.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererEventsMixin {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At("RETURN"))
    private static void irons_spellbooks$fogColor(Camera camera, float partialTick,
                                                net.minecraft.client.multiplayer.ClientLevel level,
                                                int renderDistance, float darkenAmount, CallbackInfo ci) {
        ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(
                camera, partialTick, fogRed, fogGreen, fogBlue);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        fogRed = event.getRed();
        fogGreen = event.getGreen();
        fogBlue = event.getBlue();
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void irons_spellbooks$renderFog(Camera camera, FogRenderer.FogMode mode,
                                                   float farPlaneDistance, boolean flag, float partialTick,
                                                   CallbackInfo ci) {
        ViewportEvent.RenderFog event = new ViewportEvent.RenderFog(
                mode, camera, partialTick,
                RenderSystem.getShaderFogStart(), RenderSystem.getShaderFogEnd(),
                RenderSystem.getShaderFogShape());
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        RenderSystem.setShaderFogStart(event.getNearPlaneDistance());
        RenderSystem.setShaderFogEnd(event.getFarPlaneDistance());
        RenderSystem.setShaderFogShape(event.getFogShape());
    }
}
