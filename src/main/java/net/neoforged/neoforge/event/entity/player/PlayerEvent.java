package net.neoforged.neoforge.event.entity.player;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEvent extends LivingEvent {

    public PlayerEvent(Player player) {
        super(player);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    /** Fired when a player logs in. */
    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(Player player) {
            super(player);
        }
    }

    /** Fired when a player logs out. */
    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(Player player) {
            super(player);
        }
    }

    /** Fired when a player respawns (end of respawn). */
    public static class PlayerRespawnEvent extends PlayerEvent {
        private final boolean endConquered;

        public PlayerRespawnEvent(Player player, boolean endConquered) {
            super(player);
            this.endConquered = endConquered;
        }

        public boolean isEndConquered() {
            return endConquered;
        }
    }

    /** Fired when player changes dimension. */
    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        private final ResourceKey<Level> fromDim;
        private final ResourceKey<Level> toDim;

        public PlayerChangedDimensionEvent(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }

        public ResourceKey<Level> getFrom() {
            return fromDim;
        }

        public ResourceKey<Level> getTo() {
            return toDim;
        }
    }

    /** Fired when player clones (death/dimension return). */
    public static class Clone extends PlayerEvent {
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player newPlayer, Player original, boolean wasDeath) {
            super(newPlayer);
            this.original = original;
            this.wasDeath = wasDeath;
        }

        public Player getOriginal() {
            return original;
        }

        public boolean isWasDeath() {
            return wasDeath;
        }
    }

    /** Fired when a player starts tracking an entity. */
    public static class StartTracking extends PlayerEvent {
        private final Entity target;

        public StartTracking(Player player, Entity target) {
            super(player);
            this.target = target;
        }

        public Entity getTarget() {
            return target;
        }
    }

    /** Fired when a player stops tracking an entity. */
    public static class StopTracking extends PlayerEvent {
        private final Entity target;

        public StopTracking(Player player, Entity target) {
            super(player);
            this.target = target;
        }

        public Entity getTarget() {
            return target;
        }
    }

    /** Fired on harvest check; can force harvestability. */
    public static class HarvestCheck extends PlayerEvent implements ICancellableEvent {
        private final net.minecraft.world.level.block.state.BlockState targetBlock;
        private boolean canHarvest;

        public HarvestCheck(Player player, net.minecraft.world.level.block.state.BlockState targetBlock, boolean canHarvest) {
            super(player);
            this.targetBlock = targetBlock;
            this.canHarvest = canHarvest;
        }

        public net.minecraft.world.level.block.state.BlockState getTargetBlock() {
            return targetBlock;
        }

        public boolean canHarvest() {
            return canHarvest;
        }

        public void setCanHarvest(boolean canHarvest) {
            this.canHarvest = canHarvest;
        }
    }

    /** Fired computing break speed. */
    public static class BreakSpeed extends PlayerEvent implements ICancellableEvent {
        private final net.minecraft.world.level.block.state.BlockState state;
        private final float originalSpeed;
        private float newSpeed;
        private final net.minecraft.core.BlockPos pos;
        private final net.minecraft.world.level.LevelAccessor level;

        public BreakSpeed(Player player, net.minecraft.world.level.block.state.BlockState state, float originalSpeed,
                          net.minecraft.core.BlockPos pos, net.minecraft.world.level.LevelAccessor level) {
            super(player);
            this.state = state;
            this.originalSpeed = originalSpeed;
            this.newSpeed = originalSpeed;
            this.pos = pos;
            this.level = level;
        }

        public net.minecraft.world.level.block.state.BlockState getState() {
            return state;
        }

        public float getOriginalSpeed() {
            return originalSpeed;
        }

        public float getNewSpeed() {
            return newSpeed;
        }

        public void setNewSpeed(float newSpeed) {
            this.newSpeed = newSpeed;
        }

        public net.minecraft.core.BlockPos getPos() {
            return pos;
        }

        public net.minecraft.world.level.LevelAccessor getLevel() {
            return level;
        }
    }
}
