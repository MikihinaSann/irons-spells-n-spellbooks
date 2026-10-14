package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

/**
 * NeoForge's LivingConversionEvent — fired when an entity converts into
 * another type (e.g. zombie villager curing, AC's watcher/gloomoth morphs).
 */
public class LivingConversionEvent extends LivingEvent {
    private final LivingEntity outcome;

    public LivingConversionEvent(LivingEntity entity, LivingEntity outcome) {
        super(entity);
        this.outcome = outcome;
    }

    /** The entity type the original is converting into. */
    public LivingEntity getOutcome() {
        return outcome;
    }
}
