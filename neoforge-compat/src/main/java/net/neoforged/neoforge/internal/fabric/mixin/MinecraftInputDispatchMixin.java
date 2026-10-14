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
        // NeoForge: PlayerInteractEvent.RightClickEmpty fires for each hand when
        // the crosshair target is a miss (or absent) before item use is attempted.
        if (mc.player != null && (mc.hitResult == null
                || mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS)) {
            for (var hand : net.minecraft.world.InteractionHand.values()) {
                net.neoforged.neoforge.common.CommonHooks.onEmptyClick(mc.player, hand);
            }
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
        // NeoForge: PlayerInteractEvent.LeftClickEmpty fires client-side on an
        // air left-click, then a payload notifies the server which posts the
        // event there too (CommonHooks.onEmptyLeftClick).
        if (mc.player != null && (mc.hitResult == null
                || mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS)) {
            var leftClick = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty(mc.player);
            NeoForge.EVENT_BUS.post(leftClick);
            if (net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(
                    net.neoforged.neoforge.internal.fabric.EmptyClickPayload.TYPE)) {
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                        new net.neoforged.neoforge.internal.fabric.EmptyClickPayload());
            }
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
