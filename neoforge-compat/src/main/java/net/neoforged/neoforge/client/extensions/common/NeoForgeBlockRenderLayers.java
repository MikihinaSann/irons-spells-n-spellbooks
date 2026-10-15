package net.neoforged.neoforge.client.extensions.common;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

/**
 * NeoForge's {@code ItemBlockRenderTypes.setRenderLayer(Block, RenderType)} —
 * Fabric equivalent is {@link BlockRenderLayerMap}.
 */
public final class NeoForgeBlockRenderLayers {
    public static void setRenderLayer(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    private NeoForgeBlockRenderLayers() {
    }
}
