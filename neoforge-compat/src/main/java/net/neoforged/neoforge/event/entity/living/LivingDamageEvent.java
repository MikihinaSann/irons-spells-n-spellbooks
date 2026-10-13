package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Damage events for a living entity. {@code Pre} is cancellable and may adjust
 * incoming damage; {@code Post} fires after damage is applied.
 */
public abstract class LivingDamageEvent extends LivingEvent {
    private final DamageSource source;

    public LivingDamageEvent(LivingEntity entity, DamageSource source) {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }

    public static class Pre extends LivingDamageEvent implements net.neoforged.bus.api.ICancellableEvent {
        private float originalDamage;
        private float newDamage;

        public Pre(LivingEntity entity, DamageSource source, float amount) {
            super(entity, source);
            this.originalDamage = amount;
            this.newDamage = amount;
        }

        public float getOriginalDamage() {
            return originalDamage;
        }

        public float getNewDamage() {
            return newDamage;
        }

        public void setNewDamage(float newDamage) {
            this.newDamage = newDamage;
        }

        // Legacy-style aliases used by some call sites
        public float getAmount() {
            return newDamage;
        }

        public void setAmount(float amount) {
            this.newDamage = amount;
        }
    }

    public static class Post extends LivingDamageEvent {
        private final float originalDamage;
        private final float newDamage;
        private final float blockedDamage;
        private final float shieldDamage;

        public Post(LivingEntity entity, DamageSource source, float originalDamage, float newDamage, float blockedDamage, float shieldDamage) {
            super(entity, source);
            this.originalDamage = originalDamage;
            this.newDamage = newDamage;
            this.blockedDamage = blockedDamage;
            this.shieldDamage = shieldDamage;
        }

        public float getOriginalDamage() {
            return originalDamage;
        }

        public float getNewDamage() {
            return newDamage;
        }

        public float getBlockedDamage() {
            return blockedDamage;
        }

        public float getShieldDamage() {
            return shieldDamage;
        }
    }
}
