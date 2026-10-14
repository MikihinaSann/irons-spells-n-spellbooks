package net.neoforged.neoforge.event.entity.living;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

/**
 * Fired when an entity blocks damage with a shield. Allows adjusting the
 * blocked damage, the damage dealt to the shield, and whether the block is
 * considered successful. Ported from NeoForge 21.1.
 */
public class LivingShieldBlockEvent extends LivingEvent implements ICancellableEvent {
    private final DamageContainer container;
    private float dmgBlocked;
    private float shieldDamage = -1;
    private final boolean originalBlocked;
    private boolean newBlocked;

    public LivingShieldBlockEvent(LivingEntity blocker, DamageContainer container, boolean originalBlockedState) {
        super(blocker);
        this.container = container;
        this.dmgBlocked = container.getNewDamage();
        this.originalBlocked = originalBlockedState;
        this.newBlocked = originalBlockedState;
        this.shieldDamage = container.getNewDamage();
    }

    public DamageContainer getDamageContainer() {
        return this.container;
    }

    /**
     * {@return the source of the incoming damage}
     */
    public DamageSource getDamageSource() {
        return this.getDamageContainer().getSource();
    }

    /**
     * {@return the original damage that would have been dealt if unblocked}
     */
    public float getOriginalBlockedDamage() {
        return this.getDamageContainer().getNewDamage();
    }

    /**
     * {@return the amount of damage currently being blocked by the shield}
     */
    public float getBlockedDamage() {
        return Math.min(this.dmgBlocked, container.getNewDamage());
    }

    /**
     * {@return the damage that will be dealt to the shield itself}
     */
    public float shieldDamage() {
        if (newBlocked)
            return shieldDamage >= 0 ? shieldDamage : getBlockedDamage();
        return 0;
    }

    /**
     * Sets the amount of damage blocked by the shield.
     */
    public void setBlockedDamage(float blocked) {
        this.dmgBlocked = Mth.clamp(blocked, 0, this.getOriginalBlockedDamage());
    }

    /**
     * Sets the damage dealt to the shield item.
     */
    public void setShieldDamage(float damage) {
        this.shieldDamage = damage;
    }

    /**
     * {@return whether the block was originally successful}
     */
    public boolean getOriginalBlock() {
        return originalBlocked;
    }

    /**
     * {@return whether the block is currently considered successful}
     */
    public boolean getBlocked() {
        return newBlocked;
    }

    /**
     * Sets whether the block is considered successful.
     */
    public void setBlocked(boolean isBlocked) {
        this.newBlocked = isBlocked;
    }
}
