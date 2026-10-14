package net.neoforged.neoforge.event.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge's BonemealEvent — fired when bonemeal is used on a block before
 * the vanilla grow logic; cancel to skip it.
 */
public class BonemealEvent extends PlayerEvent implements ICancellableEvent {
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final ItemStack stack;

    public BonemealEvent(Player player, Level level, BlockPos pos, BlockState state, ItemStack stack) {
        super(player);
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.stack = stack;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public ItemStack getStack() {
        return stack;
    }

    /** The player applying the bonemeal. */
    public Player getPlayer() {
        return getEntity();
    }

    /** Whether the bonemeal application proceeded (not canceled). */
    public boolean isSuccessful() {
        return !isCanceled();
    }
}
