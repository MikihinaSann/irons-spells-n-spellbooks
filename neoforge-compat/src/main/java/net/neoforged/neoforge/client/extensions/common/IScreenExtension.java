package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * NeoForge patches getGuiLeft()/getGuiTop() onto AbstractContainerScreen; our
 * port injects this interface onto the screen via Loom injected_interfaces.
 */
public interface IScreenExtension {
    private AbstractContainerScreen<?> self() {
        return (AbstractContainerScreen<?>) this;
    }

    default int getGuiLeft() {
        return self().leftPos;
    }

    default int getGuiTop() {
        return self().topPos;
    }
}
