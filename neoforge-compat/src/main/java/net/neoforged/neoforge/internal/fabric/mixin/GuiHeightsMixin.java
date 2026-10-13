package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.GuiHeights;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge stores {@code leftHeight}/{@code rightHeight} on {@link Gui}, resets
 * them to 39 each frame and lets vanilla HUD layers grow them as health/armor,
 * food/vehicle hearts and air render. Our overlays render after every vanilla
 * layer (HudRenderCallback fires at the tail of {@code Gui.render}), so at HEAD
 * we seed {@link GuiHeights} with the values NeoForge would have accumulated.
 */
@Mixin(Gui.class)
public abstract class GuiHeightsMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void irons_spellbooks$seedGuiHeights(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GuiHeights.leftHeight = 39;
        GuiHeights.rightHeight = 39;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        // creative/spectator: vanilla skips renderPlayerHealth entirely
        if (player == null || minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) {
            return;
        }
        // left: health rows (vanilla renderPlayerHealth math) + armor row
        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), Mth.ceil(player.getHealth()));
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int rows = Mth.ceil((maxHealth + absorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (rows - 2), 3);
        GuiHeights.leftHeight += (rows - 1) * rowHeight + 10;
        if (player.getArmorValue() > 0) {
            GuiHeights.leftHeight += 10;
        }
        // right: food row, or vehicle heart rows when riding a living entity
        int vehicleHearts = 0;
        if (player.getVehicle() instanceof LivingEntity vehicle && vehicle.showVehicleHealth()) {
            vehicleHearts = Math.min((int) (vehicle.getMaxHealth() + 0.5F) / 2, 30);
        }
        GuiHeights.rightHeight += vehicleHearts == 0 ? 10 : 10 * (int) Math.ceil(vehicleHearts / 10.0);
        // air bubbles row
        if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply()) {
            GuiHeights.rightHeight += 10;
        }
    }
}
