package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when an entity lands after falling. Canceling negates the fall damage.
 * Ported from NeoForge 21.1.
 */
public class LivingFallEvent extends LivingEvent implements ICancellableEvent {
    private float distance;
    private float damageMultiplier;

    public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
        super(entity);
        this.setDistance(distance);
        this.setDamageMultiplier(damageMultiplier);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }
}
