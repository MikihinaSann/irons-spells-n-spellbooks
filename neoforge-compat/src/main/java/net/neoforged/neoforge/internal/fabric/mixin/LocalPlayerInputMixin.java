package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link MovementInputUpdateEvent} after vanilla ticks the player's input.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerInputMixin {
    @Shadow
    public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void irons_spellbooks$movementInput(CallbackInfo ci) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                new MovementInputUpdateEvent((LocalPlayer) (Object) this, this.input));
    }
}
