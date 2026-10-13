package net.neoforged.neoforge.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge's block-level capability provider - resolves a capability for a
 * block position (with optional block entity and context, e.g. Direction side).
 */
@FunctionalInterface
public interface IBlockCapabilityProvider<T, C> {
    @Nullable
    T getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable C context);
}
