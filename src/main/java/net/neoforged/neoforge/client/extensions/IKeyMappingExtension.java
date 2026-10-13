package net.neoforged.neoforge.client.extensions;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * NeoForge's KeyMapping patches — injected onto KeyMapping via Loom
 * {@code injected_interfaces}.
 */
public interface IKeyMappingExtension {
    private KeyMapping self() {
        return (KeyMapping) this;
    }

    /** Currently bound key. */
    default InputConstants.Key getKey() {
        return self().key;
    }

    /** Whether the mapping is currently pressed and bound to the given key. */
    default boolean isActiveAndMatches(InputConstants.Key key) {
        return self().isDown() && self().key == key;
    }
}
