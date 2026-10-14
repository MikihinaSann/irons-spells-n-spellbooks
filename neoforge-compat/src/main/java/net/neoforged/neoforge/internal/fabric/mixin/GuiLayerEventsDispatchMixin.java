package net.neoforged.neoforge.internal.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Fires NeoForge's {@link RenderGuiLayerEvent} Pre/Post around the vanilla GUI
 * layers, keyed by {@link VanillaGuiLayers} names.
 *
 * <p>Vanilla's player-stat cluster (armor/food/health/air) lives inside one
 * method, so those four named layers share its boundary: all Pre events fire at
 * HEAD (canceling any of them skips the cluster) and all Post events fire at
 * RETURN. Every other named layer maps 1:1 onto a vanilla render method and
 * gets exact semantics.
 */
@Mixin(Gui.class)
public abstract class GuiLayerEventsDispatchMixin {

    private static boolean neoforge$pre(ResourceLocation name, GuiGraphics graphics, DeltaTracker delta) {
        var event = new RenderGuiLayerEvent.Pre(graphics, delta, name, null);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    private static void neoforge$post(ResourceLocation name, GuiGraphics graphics, DeltaTracker delta) {
        NeoForge.EVENT_BUS.post(new RenderGuiLayerEvent.Post(graphics, delta, name, null));
    }

    // ---- exact 1:1 layers ----

    @Inject(method = "renderCameraOverlays", at = @At("HEAD"), cancellable = true)
    private void neoforge$cameraOverlaysPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.CAMERA_OVERLAYS, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderCameraOverlays", at = @At("RETURN"))
    private void neoforge$cameraOverlaysPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.CAMERA_OVERLAYS, graphics, delta);
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void neoforge$crosshairPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.CROSSHAIR, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    private void neoforge$crosshairPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.CROSSHAIR, graphics, delta);
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
    private void neoforge$hotbarPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.HOTBAR, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("RETURN"))
    private void neoforge$hotbarPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.HOTBAR, graphics, delta);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void neoforge$experienceBarPre(GuiGraphics graphics, int x, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.EXPERIENCE_BAR, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer())) ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"))
    private void neoforge$experienceBarPost(GuiGraphics graphics, int x, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.EXPERIENCE_BAR, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer());
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void neoforge$jumpMeterPre(net.minecraft.world.entity.PlayerRideableJumping mount,
            GuiGraphics graphics, int x, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.JUMP_METER, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer())) ci.cancel();
    }

    @Inject(method = "renderJumpMeter", at = @At("RETURN"))
    private void neoforge$jumpMeterPost(net.minecraft.world.entity.PlayerRideableJumping mount,
            GuiGraphics graphics, int x, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.JUMP_METER, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer());
    }

    // ---- player-stat cluster (armor -> food -> health -> air share renderPlayerHealth) ----

    private static final ResourceLocation[] STAT_LAYERS = {
            VanillaGuiLayers.ARMOR_LEVEL,
            VanillaGuiLayers.FOOD_LEVEL,
            VanillaGuiLayers.PLAYER_HEALTH,
            VanillaGuiLayers.AIR_LEVEL,
    };

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void neoforge$playerHealthPre(GuiGraphics graphics, CallbackInfo ci) {
        DeltaTracker delta = net.minecraft.client.Minecraft.getInstance().getTimer();
        for (ResourceLocation layer : STAT_LAYERS) {
            if (neoforge$pre(layer, graphics, delta)) {
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "renderPlayerHealth", at = @At("RETURN"))
    private void neoforge$playerHealthPost(GuiGraphics graphics, CallbackInfo ci) {
        DeltaTracker delta = net.minecraft.client.Minecraft.getInstance().getTimer();
        for (ResourceLocation layer : STAT_LAYERS) {
            neoforge$post(layer, graphics, delta);
        }
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void neoforge$vehicleHealthPre(GuiGraphics graphics, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.VEHICLE_HEALTH, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer())) ci.cancel();
    }

    @Inject(method = "renderVehicleHealth", at = @At("RETURN"))
    private void neoforge$vehicleHealthPost(GuiGraphics graphics, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.VEHICLE_HEALTH, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer());
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void neoforge$selectedItemNamePre(GuiGraphics graphics, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.SELECTED_ITEM_NAME, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer())) ci.cancel();
    }

    @Inject(method = "renderSelectedItemName", at = @At("RETURN"))
    private void neoforge$selectedItemNamePost(GuiGraphics graphics, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.SELECTED_ITEM_NAME, graphics,
                net.minecraft.client.Minecraft.getInstance().getTimer());
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void neoforge$experienceLevelPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.EXPERIENCE_LEVEL, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderExperienceLevel", at = @At("RETURN"))
    private void neoforge$experienceLevelPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.EXPERIENCE_LEVEL, graphics, delta);
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void neoforge$effectsPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.EFFECTS, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void neoforge$effectsPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.EFFECTS, graphics, delta);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void neoforge$scoreboardPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.SCOREBOARD_SIDEBAR, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("RETURN"))
    private void neoforge$scoreboardPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.SCOREBOARD_SIDEBAR, graphics, delta);
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void neoforge$overlayMessagePre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.OVERLAY_MESSAGE, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderOverlayMessage", at = @At("RETURN"))
    private void neoforge$overlayMessagePost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.OVERLAY_MESSAGE, graphics, delta);
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void neoforge$titlePre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.TITLE, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderTitle", at = @At("RETURN"))
    private void neoforge$titlePost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.TITLE, graphics, delta);
    }

    @Inject(method = "renderChat", at = @At("HEAD"), cancellable = true)
    private void neoforge$chatPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.CHAT, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderChat", at = @At("RETURN"))
    private void neoforge$chatPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.CHAT, graphics, delta);
    }

    @Inject(method = "renderTabList", at = @At("HEAD"), cancellable = true)
    private void neoforge$tabListPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.TAB_LIST, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderTabList", at = @At("RETURN"))
    private void neoforge$tabListPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.TAB_LIST, graphics, delta);
    }

    @Inject(method = "renderSavingIndicator", at = @At("HEAD"), cancellable = true)
    private void neoforge$savingIndicatorPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.SAVING_INDICATOR, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderSavingIndicator", at = @At("RETURN"))
    private void neoforge$savingIndicatorPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.SAVING_INDICATOR, graphics, delta);
    }

    @Inject(method = "renderSleepOverlay", at = @At("HEAD"), cancellable = true)
    private void neoforge$sleepOverlayPre(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (neoforge$pre(VanillaGuiLayers.SLEEP_OVERLAY, graphics, delta)) ci.cancel();
    }

    @Inject(method = "renderSleepOverlay", at = @At("RETURN"))
    private void neoforge$sleepOverlayPost(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        neoforge$post(VanillaGuiLayers.SLEEP_OVERLAY, graphics, delta);
    }
}
