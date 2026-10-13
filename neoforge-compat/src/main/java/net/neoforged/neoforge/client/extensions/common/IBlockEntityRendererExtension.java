package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * NeoForge's {@code BlockEntityRenderer} extension — injected onto
 * {@code net.minecraft.client.renderer.blockentity.BlockEntityRenderer}.
 */
public interface IBlockEntityRendererExtension<T extends BlockEntity> {
    default AABB getRenderBoundingBox(T blockEntity) {
        return new AABB(blockEntity.getBlockPos());
    }
}
