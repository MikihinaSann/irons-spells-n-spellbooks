package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

public class CriticalHitEvent extends Event {
    private final Player player;
    private final Entity target;
    private final boolean isVanillaCritical;
    private float damageMultiplier;
    private final float vanillaMultiplier;

    public CriticalHitEvent(Player player, Entity target, float damageMultiplier, boolean isVanillaCritical) {
        this.player = player;
        this.target = target;
        this.damageMultiplier = damageMultiplier;
        this.vanillaMultiplier = damageMultiplier;
        this.isVanillaCritical = isVanillaCritical;
    }

    public Player getEntity() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    public boolean isVanillaCritical() {
        return isVanillaCritical;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public float getVanillaMultiplier() {
        return vanillaMultiplier;
    }

    public boolean isCriticalHit() {
        return damageMultiplier > 0;
    }

    public void setCriticalHit(boolean criticalHit) {
        this.damageMultiplier = criticalHit ? vanillaMultiplier : 0;
    }
}
