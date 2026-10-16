package net.neoforged.neoforge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Factory producing a mod's config screen — registered via
 * {@code ModLoadingContext#registerExtensionPoint}. On Fabric this is a
 * stored no-op hook unless a mods-menu bridge consumes it.
 */
@FunctionalInterface
public interface IConfigScreenFactory {
    Screen createScreen(Minecraft minecraft, Screen parent);
}
