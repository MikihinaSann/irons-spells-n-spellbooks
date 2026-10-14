package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

public abstract class EntityEvent extends Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public net.minecraft.world.level.LevelAccessor getLevel() {
        return entity.level();
    }

    /**
     * Fired when an entity's dimensions are (re)computed, allowing the new
     * size to be replaced — mirrors NeoForge's {@code EntityEvent.Size}.
     */
    public static class Size extends EntityEvent {
        private final net.minecraft.world.entity.EntityDimensions oldSize;
        private net.minecraft.world.entity.EntityDimensions newSize;
        private final float oldEyeHeight;
        private float newEyeHeight;

        public Size(Entity entity, net.minecraft.world.entity.EntityDimensions oldSize,
                net.minecraft.world.entity.EntityDimensions newSize,
                float oldEyeHeight, float newEyeHeight) {
            super(entity);
            this.oldSize = oldSize;
            this.newSize = newSize;
            this.oldEyeHeight = oldEyeHeight;
            this.newEyeHeight = newEyeHeight;
        }

        public net.minecraft.world.entity.EntityDimensions getOldSize() {
            return oldSize;
        }

        public net.minecraft.world.entity.EntityDimensions getNewSize() {
            return newSize;
        }

        public void setNewSize(net.minecraft.world.entity.EntityDimensions size) {
            this.newSize = size;
        }

        public float getOldEyeHeight() {
            return oldEyeHeight;
        }

        public float getNewEyeHeight() {
            return newEyeHeight;
        }

        public void setNewEyeHeight(float eyeHeight) {
            this.newEyeHeight = eyeHeight;
        }
    }
}
