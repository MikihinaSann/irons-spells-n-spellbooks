package net.neoforged.neoforge.client.extensions;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * NeoForge's BakedModel patches — injected onto BakedModel via Loom
 * {@code injected_interfaces}.
 */
public interface IBakedModelExtension {
    private BakedModel self() {
        return (BakedModel) this;
    }

    /**
     * Block render types the model can render with; vanilla exposes a single
     * render type per block state.
     */
    default ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData modelData) {
        RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(state);
        return ChunkRenderTypeSet.of(renderType);
    }

    /**
     * Item render types for the given stack.
     */
    default java.util.List<RenderType> getRenderTypes(net.minecraft.world.item.ItemStack itemStack, boolean fabulous) {
        return java.util.List.of(ItemBlockRenderTypes.getRenderType(itemStack, fabulous));
    }
}
