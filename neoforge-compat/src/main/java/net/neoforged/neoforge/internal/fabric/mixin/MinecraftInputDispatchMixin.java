package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link InputEvent.InteractionKeyMappingTriggered} for the attack/use/pick
 * keymappings. NeoForge posts these from {@code Minecraft#handleKeybinds}; the
 * Fabric port posts them at the vanilla action entry points. Cancellation
 * suppresses the action entirely (used to block use/attack while casting).
 */
@Mixin(Minecraft.class)
public abstract class MinecraftInputDispatchMixin {
    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$useItem(CallbackInfo ci) {
        var mc = (Minecraft) (Object) this;
        var event = new InputEvent.InteractionKeyMappingTriggered(1, mc.options.keyUse, null);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$attack(CallbackInfoReturnable<Boolean> cir) {
        var mc = (Minecraft) (Object) this;
        var event = new InputEvent.InteractionKeyMappingTriggered(0, mc.options.keyAttack, null);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$pickBlock(CallbackInfo ci) {
        var mc = (Minecraft) (Object) this;
        var event = new InputEvent.InteractionKeyMappingTriggered(2, mc.options.keyPickItem, null);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
