package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link InputEvent.MouseScrollingEvent} when the hotbar scroll happens
 * in-game. NeoForge posts it from MouseHandler; in 1.21.1 the GLFW scroll
 * callback drives {@link Inventory#swapPaint} directly.
 */
@Mixin(Inventory.class)
public abstract class InputEventsDispatchMixin {
    @Inject(method = "swapPaint", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$mouseScroll(double scrollAmount, CallbackInfo ci) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || (Object) this != mc.player.getInventory()) {
            return;
        }
        var mouse = mc.mouseHandler;
        var event = new InputEvent.MouseScrollingEvent(0, scrollAmount,
                mouse.isLeftPressed(), mouse.isMiddlePressed(), mouse.isRightPressed(),
                mouse.xpos(), mouse.ypos());
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
