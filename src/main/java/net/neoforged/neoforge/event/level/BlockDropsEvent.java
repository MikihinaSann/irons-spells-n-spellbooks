package net.neoforged.neoforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fired when a block's drops are being harvested. Mirrors NeoForge's
 * {@code BlockDropsEvent}.
 */
public class BlockDropsEvent extends Event implements ICancellableEvent {
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;
    @Nullable
    private final BlockEntity blockEntity;
    @Nullable
    private final Entity breaker;
    private final ItemStack tool;
    private final boolean isSilkTouching;
    private final List<ItemEntity> drops;
    private int dropExperience;

    public BlockDropsEvent(LevelAccessor level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
                           @Nullable Entity breaker, ItemStack tool, List<ItemEntity> drops,
                           boolean isSilkTouching, int dropExperience) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.blockEntity = blockEntity;
        this.breaker = breaker;
        this.tool = tool;
        this.drops = drops;
        this.isSilkTouching = isSilkTouching;
        this.dropExperience = dropExperience;
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

    @Nullable
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Nullable
    public Entity getBreaker() {
        return breaker;
    }

    @Nullable
    public Player getPlayer() {
        return breaker instanceof Player p ? p : null;
    }

    public ItemStack getTool() {
        return tool;
    }

    public boolean isSilkTouching() {
        return isSilkTouching;
    }

    public List<ItemEntity> getDrops() {
        return drops;
    }

    public int getDroppedExperience() {
        return dropExperience;
    }

    public int getDropExperience() {
        return dropExperience;
    }

    public void setDroppedExperience(int exp) {
        this.dropExperience = exp;
    }

    public void setDropExperience(int exp) {
        this.dropExperience = exp;
    }
}
