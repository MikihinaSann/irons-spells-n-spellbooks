package net.neoforged.neoforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;

public enum KeyModifier {
    CONTROL, SHIFT, ALT, NONE;

    public static KeyModifier getFromType(InputConstants.Type type) {
        return switch (type) {
            case KEYSYM -> NONE;
            case SCANCODE -> NONE;
            default -> NONE;
        };
    }
}
