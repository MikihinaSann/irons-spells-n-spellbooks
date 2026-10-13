package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;

public class LivingKnockBackEvent extends LivingEvent implements ICancellableEvent {
    private float strength;
    private double ratioX;
    private double ratioZ;
    private float originalStrength;
    private double originalRatioX;
    private double originalRatioZ;
    private Entity attacker;

    public LivingKnockBackEvent(LivingEntity entity, float strength, double ratioX, double ratioZ) {
        super(entity);
        this.strength = strength;
        this.ratioX = ratioX;
        this.ratioZ = ratioZ;
        this.originalStrength = strength;
        this.originalRatioX = ratioX;
        this.originalRatioZ = ratioZ;
    }

    public LivingKnockBackEvent(LivingEntity entity, Entity attacker, float strength, double ratioX, double ratioZ) {
        this(entity, strength, ratioX, ratioZ);
        this.attacker = attacker;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public double getRatioX() {
        return ratioX;
    }

    public void setRatioX(double ratioX) {
        this.ratioX = ratioX;
    }

    public double getRatioZ() {
        return ratioZ;
    }

    public void setRatioZ(double ratioZ) {
        this.ratioZ = ratioZ;
    }

    public float getOriginalStrength() {
        return originalStrength;
    }

    public double getOriginalRatioX() {
        return originalRatioX;
    }

    public double getOriginalRatioZ() {
        return originalRatioZ;
    }

    public Entity getAttacker() {
        return attacker;
    }
}
