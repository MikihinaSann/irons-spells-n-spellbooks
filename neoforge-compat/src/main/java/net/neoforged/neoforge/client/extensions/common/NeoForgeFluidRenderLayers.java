package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NeoForge's {@code ItemBlockRenderTypes.setRenderLayer(Fluid, RenderType)} —
 * per-fluid render layers consulted by the {@code getRenderLayer} mixin.
 */
public final class NeoForgeFluidRenderLayers {
    private static final Map<Fluid, RenderType> LAYERS = new ConcurrentHashMap<>();

    private NeoForgeFluidRenderLayers() {
    }

    public static void setRenderLayer(Fluid fluid, RenderType renderType) {
        LAYERS.put(fluid, renderType);
    }

    public static RenderType getRenderLayer(Fluid fluid) {
        return LAYERS.get(fluid);
    }
}
