package net.neoforged.neoforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Custom overlay drawn for an item stack in GUIs — the NeoForge replacement
 * for vanilla's hardcoded durability/cooldown decorations.
 */
@FunctionalInterface
public interface ItemDecorator {
    boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset);
}
