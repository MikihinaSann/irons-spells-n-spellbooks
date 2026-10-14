package net.neoforged.neoforge.event.level;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

/**
 * Fires on the logical server around vanilla explosions.
 */
public abstract class ExplosionEvent extends Event {
    private final Level level;
    private final Explosion explosion;

    public ExplosionEvent(Level level, Explosion explosion) {
        this.level = level;
        this.explosion = explosion;
    }

    public Level getLevel() { return level; }
    public Explosion getExplosion() { return explosion; }

    /** Fired before the explosion processes — cancellation suppresses it. */
    public static class Start extends ExplosionEvent implements net.neoforged.bus.api.ICancellableEvent {
        public Start(Level level, Explosion explosion) {
            super(level, explosion);
        }
    }

    /** Fired after the affected lists are computed; both are mutable. */
    public static class Detonate extends ExplosionEvent {
        private final List<Entity> entityList;
        private final List<BlockPos> blockList;

        public Detonate(Level level, Explosion explosion, List<Entity> entityList) {
            this(level, explosion, entityList, java.util.Collections.emptyList());
        }

        public Detonate(Level level, Explosion explosion, List<Entity> entityList, List<BlockPos> blockList) {
            super(level, explosion);
            this.entityList = entityList;
            this.blockList = blockList;
        }

        public List<Entity> getAffectedEntities() { return entityList; }
        public List<BlockPos> getAffectedBlocks() { return blockList; }
    }
}
