package io.redspace.ironsspellbooks.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Fabric analogue of NeoForge's {@code separate_transforms} baked model:
 * swaps in a different baked model per {@link ItemDisplayContext}.
 * Delegation target is resolved at render time via {@link #getModelForContext}.
 */
public class SeparateTransformsModel implements BakedModel {
    private final BakedModel base;
    private final Map<ItemDisplayContext, BakedModel> perspectives;

    public SeparateTransformsModel(BakedModel base, Map<ItemDisplayContext, BakedModel> perspectives) {
        this.base = base;
        this.perspectives = perspectives;
    }

    public BakedModel getModelForContext(ItemDisplayContext context) {
        return perspectives.getOrDefault(context, base);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return base.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return base.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return base.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return base.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return base.getOverrides();
    }
}
