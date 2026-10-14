package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link ComputeFovModifierEvent} when the client's fov modifier is
 * computed — mirrors NeoForge's hook into the fov pipeline.
 */
@Mixin(AbstractClientPlayer.class)
public abstract class FovModifierDispatchMixin {

    @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
    private void neoforgeCompat$fovModifier(CallbackInfoReturnable<Float> cir) {
        ComputeFovModifierEvent event = new ComputeFovModifierEvent(
                (AbstractClientPlayer) (Object) this, cir.getReturnValue());
        NeoForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.getNewFovModifier());
    }
}
