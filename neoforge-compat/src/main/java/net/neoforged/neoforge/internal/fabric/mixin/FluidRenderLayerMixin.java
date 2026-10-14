package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.NeoForgeFluidRenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies {@link NeoForgeFluidRenderLayers} registrations in
 * {@code ItemBlockRenderTypes.getRenderLayer} — NeoForge's
 * {@code setRenderLayer(Fluid, RenderType)} hook.
 */
@Mixin(ItemBlockRenderTypes.class)
public abstract class FluidRenderLayerMixin {

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private static void neoforgeCompat$fluidRenderLayer(FluidState state,
            CallbackInfoReturnable<RenderType> cir) {
        RenderType custom = NeoForgeFluidRenderLayers.getRenderLayer(state.getType());
        if (custom != null) {
            cir.setReturnValue(custom);
        }
    }
}
