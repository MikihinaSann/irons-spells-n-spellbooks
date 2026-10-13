package net.neoforged.neoforge.client.settings;

import net.minecraft.client.KeyMapping;

/**
 * When a key mapping is active.
 */
public enum KeyConflictContext implements net.neoforged.neoforge.client.settings.IKeyConflictContext {
    /** Keybinding is always active (default). */
    UNIVERSAL,
    /** Only when a GUI screen is open. */
    GUI,
    /** Only when in-game (no screen). */
    IN_GAME;

    @Override
    public boolean isActive() {
        return switch (this) {
            case UNIVERSAL -> true;
            case GUI -> net.minecraft.client.Minecraft.getInstance().screen != null;
            case IN_GAME -> net.minecraft.client.Minecraft.getInstance().screen == null;
        };
    }

    @Override
    public boolean conflicts(IKeyConflictContext other) {
        return this == other || this == UNIVERSAL || other == UNIVERSAL;
    }
}
