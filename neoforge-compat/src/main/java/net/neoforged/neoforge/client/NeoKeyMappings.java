package net.neoforged.neoforge.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/** NeoForge's {@code KeyMapping.isActiveAndMatches(InputConstants.Key)}. */
public final class NeoKeyMappings {
    private NeoKeyMappings() {
    }

    public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key key) {
        return mapping.isDown() && mapping.key.equals(key);
    }
}
