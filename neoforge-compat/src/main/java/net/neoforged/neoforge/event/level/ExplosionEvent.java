package net.neoforged.neoforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import java.util.List;

public abstract class ExplosionEvent extends Event {
    private final Level level;
    private final Explosion explosion;

    public ExplosionEvent(Level level, Explosion explosion) {
        this.level = level;
        this.explosion = explosion;
    }

    public Level getLevel() {
        return level;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    /** Fired before the explosion starts; cancelling prevents it. */
    public static class Start extends ExplosionEvent implements ICancellableEvent {
        public Start(Level level, Explosion explosion) {
            super(level, explosion);
        }
    }

    /** Fired after blocks/entities computed; lists are mutable. */
    public static class Detonate extends ExplosionEvent {
        private final List<BlockPos> affectedBlocks;
        private final List<Entity> affectedEntities;

        public Detonate(Level level, Explosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities) {
            super(level, explosion);
            this.affectedBlocks = affectedBlocks;
            this.affectedEntities = affectedEntities;
        }

        public List<BlockPos> getAffectedBlocks() {
            return affectedBlocks;
        }

        public List<Entity> getAffectedEntities() {
            return affectedEntities;
        }
    }
}
