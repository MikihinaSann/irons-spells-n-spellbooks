package net.neoforged.neoforge.common.extensions;

/**
 * NeoForge's TooltipFlag extension — key-state queries patched onto TooltipFlag.
 */
public interface ITooltipFlagExtension {

    default boolean hasShiftDown() {
        return net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    default boolean hasAltDown() {
        return net.minecraft.client.gui.screens.Screen.hasAltDown();
    }
}
