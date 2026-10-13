package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Fires {@link CustomizeGuiOverlayEvent.BossEventProgress} per boss bar inside
 * {@code BossHealthOverlay#render}. Cancellation suppresses the vanilla bar and
 * its label (the next {@code drawString} in the same loop iteration).
 */
@Mixin(BossHealthOverlay.class)
public abstract class BossBarDispatchMixin {
    @Unique
    private boolean irons_spellbooks$suppressBar;

    @WrapOperation(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"))
    private void irons_spellbooks$bossBar(BossHealthOverlay self, GuiGraphics guiGraphics, int x, int y,
                                          net.minecraft.world.BossEvent bossEvent, Operation<Void> operation,
                                          @Local LerpingBossEvent lerpingBossEvent) {
        var event = new CustomizeGuiOverlayEvent.BossEventProgress(guiGraphics,
                net.minecraft.client.Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true),
                guiGraphics.guiWidth(), guiGraphics.guiHeight(), lerpingBossEvent, x, y, 19);
        NeoForge.EVENT_BUS.post(event);
        this.irons_spellbooks$suppressBar = event.isCanceled();
        if (!event.isCanceled()) {
            operation.call(self, guiGraphics, event.getX(), event.getY(), bossEvent);
        }
    }

    @WrapOperation(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
    private int irons_spellbooks$bossName(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font,
                                          net.minecraft.network.chat.Component text, int x, int y, int color,
                                          Operation<Integer> operation) {
        if (this.irons_spellbooks$suppressBar) {
            this.irons_spellbooks$suppressBar = false;
            return 0;
        }
        return operation.call(guiGraphics, font, text, x, y, color);
    }
}
