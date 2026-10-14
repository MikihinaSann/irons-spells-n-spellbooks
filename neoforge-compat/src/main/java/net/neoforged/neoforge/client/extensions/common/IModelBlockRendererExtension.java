package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * NeoForge's {@code ModelBlockRenderer} extension — injected onto
 * {@code net.minecraft.client.renderer.block.ModelBlockRenderer}. NeoForge
 * patches a {@code tesselateBlock} overload that accepts the model data and the
 * render type being drawn; our default drops both (the vertex consumer is
 * already bound to the render type).
 */
public interface IModelBlockRendererExtension {
    private ModelBlockRenderer self() {
        return (ModelBlockRenderer) (Object) this;
    }

    default void tesselateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos,
            PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random, long seed,
            int packedOverlay, ModelData modelData, RenderType renderType) {
        self().tesselateBlock(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay);
    }
}
