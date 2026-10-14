package net.neoforged.neoforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge's FluidPlaceBlockEvent — fired when a fluid interaction replaces a
 * block (e.g. lava + water forming stone); listeners may alter the result.
 */
public class FluidPlaceBlockEvent extends Event implements ICancellableEvent {
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockPos liquidPos;
    private BlockState newState;
    private final BlockState originalState;

    public FluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos,
            BlockState newState) {
        this.level = level;
        this.pos = pos;
        this.liquidPos = liquidPos;
        this.newState = newState;
        this.originalState = level.getBlockState(pos);
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockPos getLiquidPos() {
        return liquidPos;
    }

    public BlockState getNewState() {
        return newState;
    }

    public void setNewState(BlockState newState) {
        this.newState = newState;
    }

    public BlockState getOriginalState() {
        return originalState;
    }
}
