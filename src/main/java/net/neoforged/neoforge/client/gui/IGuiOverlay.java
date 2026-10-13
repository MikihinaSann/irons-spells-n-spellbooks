package net.neoforged.neoforge.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public interface IGuiOverlay extends net.minecraft.client.gui.LayeredDraw.Layer {
    @Override
    void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
