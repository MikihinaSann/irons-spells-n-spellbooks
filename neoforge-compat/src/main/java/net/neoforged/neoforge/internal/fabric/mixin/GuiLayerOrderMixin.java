package net.neoforged.neoforge.internal.fabric.mixin;

import net.neoforged.neoforge.internal.fabric.GuiLayerSlots;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Places {@link GuiLayerSlots} registrations at the same positions NeoForge's
 * {@code RegisterGuiLayersEvent} would have given them inside the vanilla HUD
 * chain (each slot renders at the z of the surrounding vanilla layer).
 */
@Mixin(Gui.class)
public abstract class GuiLayerOrderMixin {

    @Shadow
    @Final
    private LayeredDraw layers;

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    private void irons_spellbooks$beforeCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiLayerSlots.render(GuiLayerSlots.Slot.BEFORE_CROSSHAIR, guiGraphics, deltaTracker);
    }

    // RETURN covers renderCrosshair's early returns (non-first-person / spectator)
    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    private void irons_spellbooks$afterCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiLayerSlots.render(GuiLayerSlots.Slot.AFTER_CROSSHAIR, guiGraphics, deltaTracker);
    }

    // renderHotbarAndDecorations: hotbar -> jump/xp bar -> if (canHurtPlayer)
    // renderPlayerHealth -> renderVehicleHealth -> renderSelectedItemName.
    // canHurtPlayer is invoked exactly once in this method.
    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;canHurtPlayer()Z"))
    private void irons_spellbooks$afterXpBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiLayerSlots.render(GuiLayerSlots.Slot.AFTER_XP, guiGraphics, deltaTracker);
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            shift = At.Shift.AFTER))
    private void irons_spellbooks$afterStatusBars(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiLayerSlots.render(GuiLayerSlots.Slot.AFTER_STATUS, guiGraphics, deltaTracker);
    }

    // ABOVE_ALL becomes one extra layer at the end of vanilla's chain, stepping
    // +200 z between registrations just like the built-in layers do.
    @Inject(method = "<init>", at = @At("TAIL"))
    private void irons_spellbooks$appendTopLayers(Minecraft minecraft, CallbackInfo ci) {
        this.layers.add((guiGraphics, deltaTracker) -> {
            var pose = guiGraphics.pose();
            pose.pushPose();
            for (var registration : GuiLayerSlots.get(GuiLayerSlots.Slot.TOP)) {
                registration.layer().render(guiGraphics, deltaTracker);
                pose.translate(0, 0, LayeredDraw.Z_SEPARATION);
            }
            pose.popPose();
        });
    }
}
