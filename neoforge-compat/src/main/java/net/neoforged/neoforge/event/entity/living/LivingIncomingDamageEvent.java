package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired just before damage is applied (before armor/effects reductions on NeoForge;
 * on Fabric posted at the start of {@code LivingEntity#hurtServer/hurt}).
 */
public class LivingIncomingDamageEvent extends LivingEvent implements ICancellableEvent {
    private final DamageSource source;
    private final net.neoforged.neoforge.common.damagesource.DamageContainer container;
    private float amount;

    public LivingIncomingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        super(entity);
        this.source = source;
        this.amount = amount;
        this.container = new net.neoforged.neoforge.common.damagesource.DamageContainer(source, entity, amount);
    }

    public void setInvulnerabilityTicks(int ticks) {
        getContainer().setPostAttackInvulnerabilityTicks(ticks);
    }

    public net.neoforged.neoforge.common.damagesource.DamageContainer getContainer() {
        return container;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getOriginalAmount() {
        return container.getOriginalDamage();
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public DamageSource getOriginalSource() {
        return source;
    }

    public Entity getAttacker() {
        return source.getEntity();
    }

    /** Compatibility: NeoForge wraps a DamageContainer in newer versions. */
    public DamageSource getSource0() {
        return source;
    }
}
