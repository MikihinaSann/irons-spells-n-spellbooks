package net.neoforged.neoforge.client.extensions;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * NeoForge's {@code BlockRenderDispatcher} ModelData overloads.
 */
public interface IBlockRenderDispatcherExtension {
    private BlockRenderDispatcher self() {
        return (BlockRenderDispatcher) (Object) this;
    }

    default void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter level,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            com.mojang.blaze3d.vertex.VertexConsumer consumer, ModelData modelData) {
        self().renderBreakingTexture(state, pos, level, poseStack, consumer);
    }
}
