package net.neoforged.neoforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BlockEvent extends Event {
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;

    public BlockEvent(LevelAccessor level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    /** Fired when a block is broken by a player. */
    public static class BreakEvent extends BlockEvent implements ICancellableEvent {
        private final Player player;
        private final BlockState state0;
        private int exp;

        public BreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
            super(level, pos, state);
            this.state0 = state;
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        public int getExpToDrop() {
            return exp;
        }

        public void setExpToDrop(int exp) {
            this.exp = exp;
        }
    }

    /** Fired when an entity places a block. */
    public static class EntityPlaceEvent extends BlockEvent implements ICancellableEvent {
        private final Entity entity;
        private final BlockState placedBlock;
        private final BlockState placedAgainst;

        public EntityPlaceEvent(net.minecraft.world.level.block.Block placedBlock, BlockState placedBlockState,
                                LevelAccessor world, BlockPos pos, @Nullable Entity entity, @Nullable ItemStack itemStack) {
            super(world, pos, placedBlockState);
            this.placedBlock = placedBlockState;
            this.placedAgainst = placedBlockState;
            this.entity = entity;
        }

        public EntityPlaceEvent(BlockState placedBlock, BlockState placedAgainst, LevelAccessor world,
                                BlockPos pos, @Nullable Entity entity) {
            super(world, pos, placedBlock);
            this.placedBlock = placedBlock;
            this.placedAgainst = placedAgainst;
            this.entity = entity;
        }

        @Nullable
        public Entity getEntity() {
            return entity;
        }

        public BlockState getPlacedBlock() {
            return placedBlock;
        }

        public BlockState getPlacedAgainst() {
            return placedAgainst;
        }
    }

    /** Fired when an entity places multiple blocks at once. */
    public static class EntityMultiPlaceEvent extends EntityPlaceEvent {
        private final List<BlockSnapshot> blockSnapshots;

        public EntityMultiPlaceEvent(List<BlockSnapshot> blockSnapshots, BlockState placedAgainst,
                                     LevelAccessor world, BlockPos pos, @Nullable Entity entity) {
            super(blockSnapshots.isEmpty() ? placedAgainst : blockSnapshots.get(0).getCurrentBlock(), placedAgainst, world, pos, entity);
            this.blockSnapshots = blockSnapshots;
        }

        public List<BlockSnapshot> getReplacedBlockSnapshots() {
            return blockSnapshots;
        }
    }

    /** Fired when farmland is trampled. */
    public static class FarmlandTrampleEvent extends BlockEvent implements ICancellableEvent {
        private final Entity entity;
        private final float fallDistance;

        public FarmlandTrampleEvent(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
            super(level, pos, state);
            this.fallDistance = fallDistance;
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }

        public float getFallDistance() {
            return fallDistance;
        }
    }

    /** Neighbor notification (block update) event. */
    public static class NeighborNotifyEvent extends BlockEvent implements ICancellableEvent {
        private final net.minecraft.world.level.block.Block block;
        private final net.minecraft.world.level.block.Block fromBlock;
        private final boolean notifyNeighbors;
        private final java.util.EnumSet<net.minecraft.core.Direction> notifiedSides;
        private final boolean forceRedstoneUpdate;

        public NeighborNotifyEvent(Level level, BlockPos pos, BlockState state,
                                   java.util.EnumSet<net.minecraft.core.Direction> notifiedSides,
                                   boolean forceRedstoneUpdate) {
            super(level, pos, state);
            this.block = state.getBlock();
            this.fromBlock = state.getBlock();
            this.notifyNeighbors = true;
            this.notifiedSides = notifiedSides;
            this.forceRedstoneUpdate = forceRedstoneUpdate;
        }

        public net.minecraft.world.level.block.Block getBlock() {
            return block;
        }

        public java.util.EnumSet<net.minecraft.core.Direction> getNotifiedSides() {
            return notifiedSides;
        }
    }
}
