package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Client extensions for mob effects (NeoForge API parity).
 */
public interface IClientMobEffectExtensions {
    /**
     * Whether the effect icon should render in the inventory screen.
     */
    default boolean isVisibleInInventory(MobEffectInstance instance) {
        return true;
    }

    /**
     * Whether the effect icon should render in the HUD.
     */
    default boolean isVisibleInGui(MobEffectInstance instance) {
        return true;
    }

    /**
     * Render the effect icon in the inventory screen.
     *
     * @return true to prevent default rendering, false otherwise
     */
    default boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
        return false;
    }

    /**
     * Render the effect text in the inventory screen.
     *
     * @return true to prevent default rendering, false otherwise
     */
    default boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
        return false;
    }

    /**
     * Render the effect icon in the HUD.
     *
     * @return true to prevent default rendering, false otherwise
     */
    default boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
        return false;
    }

    /**
     * Render the effect text in the HUD.
     *
     * @return true to prevent default rendering, false otherwise
     */
    default boolean renderGuiText(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
        return false;
    }
}
