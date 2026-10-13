package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class LivingEvent extends net.neoforged.neoforge.event.entity.EntityEvent {

    public LivingEvent(LivingEntity entity) {
        super(entity);
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }

    /** Fired every tick before {@code LivingEntity#tick}. */
    public static class LivingTickEvent extends LivingEvent implements ICancellableEvent {
        public LivingTickEvent(LivingEntity entity) {
            super(entity);
        }
    }

    /** Fired when a living entity is about to be converted (e.g. villager -> zombie). */
    public static class LivingConversionEvent extends LivingEvent {
        private final net.minecraft.world.entity.EntityType<?> outcome;

        public LivingConversionEvent(LivingEntity entity, net.minecraft.world.entity.EntityType<?> outcome) {
            super(entity);
            this.outcome = outcome;
        }

        public net.minecraft.world.entity.EntityType<?> getOutcome() {
            return outcome;
        }

        public static class Pre extends LivingConversionEvent implements ICancellableEvent {
            public Pre(LivingEntity entity, net.minecraft.world.entity.EntityType<?> outcome) {
                super(entity, outcome);
            }
        }

        public static class Post extends LivingConversionEvent {
            private final LivingEntity newEntity;

            public Post(LivingEntity entity, LivingEntity newEntity, net.minecraft.world.entity.EntityType<?> outcome) {
                super(entity, outcome);
                this.newEntity = newEntity;
            }

            public LivingEntity getNewEntity() {
                return newEntity;
            }
        }
    }
}
