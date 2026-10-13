package net.neoforged.neoforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Snapshot of a block state — used by {@link BlockEvent.EntityMultiPlaceEvent}.
 */
public class BlockSnapshot {
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState replacedBlock;
    private final BlockState currentBlock;
    private final int flags;

    public BlockSnapshot(LevelAccessor level, BlockPos pos, BlockState replacedBlock, BlockState currentBlock, int flags) {
        this.level = level;
        this.pos = pos;
        this.replacedBlock = replacedBlock;
        this.currentBlock = currentBlock;
        this.flags = flags;
    }

    public static BlockSnapshot create(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim,
                                       LevelAccessor level, BlockPos pos) {
        return new BlockSnapshot(level, pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getReplacedBlock() {
        return replacedBlock;
    }

    public BlockState getCurrentBlock() {
        return currentBlock;
    }

    public BlockState getState() {
        return currentBlock;
    }

    public int getFlags() {
        return flags;
    }

    public boolean restore() {
        return level.setBlock(pos, replacedBlock, flags);
    }
}
