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

    /**
     * NeoForge's {@code getParticleIcon(ModelData)} overload — the model data is
     * ignored by vanilla-equivalent models.
     */
    default net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon(ModelData data) {
        return self().getParticleIcon();
    }

    /**
     * NeoForge's {@code getQuads(state, side, rand, modelData, renderType)}
     * overload — vanilla models have no model-data or render-type awareness.
     */
    default java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(
            @org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction side,
            RandomSource rand, ModelData modelData, @org.jetbrains.annotations.Nullable RenderType renderType) {
        return self().getQuads(state, side, rand);
    }
}
