package net.neoforged.neoforge.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extra {@link RenderType}s exposed by NeoForge. Unlit variants are
 * approximated with the closest vanilla composite type — callers that pass
 * full-bright light (240) get visually identical output.
 */
public final class NeoForgeRenderTypes {
    private static final Map<ResourceLocation, RenderType> UNLIT_TRANSLUCENT = new ConcurrentHashMap<>();

    private NeoForgeRenderTypes() {
    }

    public static RenderType getUnlitTranslucent(ResourceLocation texture) {
        return UNLIT_TRANSLUCENT.computeIfAbsent(texture, RenderType::entityTranslucent);
    }

    public static RenderType getUnsortedTranslucent(ResourceLocation texture) {
        return getUnlitTranslucent(texture);
    }
}
