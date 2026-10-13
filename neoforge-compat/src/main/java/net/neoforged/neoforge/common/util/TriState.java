package net.neoforged.neoforge.common.util;

/** NeoForge TriState: TRUE / DEFAULT / FALSE with default-aware boolean resolution. */
public enum TriState {
    TRUE, DEFAULT, FALSE;

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    public boolean asBoolean(boolean def) {
        return this == DEFAULT ? def : this.isTrue();
    }

    public static TriState of(boolean value) {
        return value ? TRUE : FALSE;
    }
}
