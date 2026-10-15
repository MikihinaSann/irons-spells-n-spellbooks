package net.neoforged.neoforge.event.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when a living entity attempts to destroy a block (wither, ender dragon,
 * zombie breaking doors, modded mobs calling {@code level.destroyBlock}).
 * Canceling prevents the destruction.
 */
public class LivingDestroyBlockEvent extends LivingEvent implements ICancellableEvent {
    private final BlockPos pos;
    private final BlockState state;

    public LivingDestroyBlockEvent(LivingEntity entity, BlockPos pos, BlockState state) {
        super(entity);
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    /** NeoForge name for {@link #getState()}. */
    public BlockState getTargetBlock() {
        return state;
    }
}
