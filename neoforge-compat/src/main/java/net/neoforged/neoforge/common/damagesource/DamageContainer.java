package net.neoforged.neoforge.common.damagesource;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * NeoForge's per-hit damage container. Captures a single incoming attack's
 * context so listeners can adjust amounts/flags coherently.
 */
public class DamageContainer {
    private final DamageSource source;
    private final LivingEntity entity;
    private float originalDamage;
    private float newDamage;
    private int postAttackInvulnerabilityTicks;

    public DamageContainer(DamageSource source, LivingEntity entity, float amount) {
        this.source = source;
        this.entity = entity;
        this.originalDamage = amount;
        this.newDamage = amount;
        this.postAttackInvulnerabilityTicks = entity.invulnerableTime;
    }

    public DamageSource getSource() {
        return source;
    }

    public LivingEntity getEntity() {
        return entity;
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

    public int getPostAttackInvulnerabilityTicks() {
        return postAttackInvulnerabilityTicks;
    }

    public void setPostAttackInvulnerabilityTicks(int ticks) {
        this.postAttackInvulnerabilityTicks = ticks;
    }

    public float getModifier(IReductionFunction reductionFunction) {
        return 0;
    }

    public interface IReductionFunction {
    }
}
