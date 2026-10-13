package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.ICancellableEvent;

public class EntityTeleportEvent extends EntityEvent implements ICancellableEvent {
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final Entity entity;
    protected final double prevX;
    protected final double prevY;
    protected final double prevZ;

    public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
        super(entity);
        this.entity = entity;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.prevX = entity.getX();
        this.prevY = entity.getY();
        this.prevZ = entity.getZ();
    }

    public Vec3 getTarget() {
        return new Vec3(targetX, targetY, targetZ);
    }

    public double getTargetX() {
        return targetX;
    }

    public void setTargetX(double targetX) {
        this.targetX = targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public void setTargetY(double targetY) {
        this.targetY = targetY;
    }

    public double getTargetZ() {
        return targetZ;
    }

    public void setTargetZ(double targetZ) {
        this.targetZ = targetZ;
    }

    public Vec3 getPrev() {
        return new Vec3(prevX, prevY, prevZ);
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getPrevZ() {
        return prevZ;
    }

    /** Fired when an entity teleports via {@code /teleport} or similar commands. */
    public static class TeleportCommand extends EntityTeleportEvent {
        public TeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
            super(entity, targetX, targetY, targetZ);
        }
    }

    /** Fired when an entity teleports via chorus fruit. */
    public static class ChorusFruit extends EntityTeleportEvent {
        public ChorusFruit(net.minecraft.world.entity.LivingEntity entity, double targetX, double targetY, double targetZ) {
            super(entity, targetX, targetY, targetZ);
        }

        @Override
        public net.minecraft.world.entity.LivingEntity getEntity() {
            return (net.minecraft.world.entity.LivingEntity) super.getEntity();
        }
    }

    /** Fired when an entity teleports via ender pearl. */
    public static class EnderPearl extends EntityTeleportEvent {
        private final net.minecraft.world.entity.player.Player player;
        private final net.minecraft.world.entity.projectile.ThrownEnderpearl pearlEntity;
        private float attackDamage;

        public EnderPearl(net.minecraft.world.entity.player.Player entity, double targetX, double targetY, double targetZ,
                          net.minecraft.world.entity.projectile.ThrownEnderpearl pearlEntity, float attackDamage) {
            super(entity, targetX, targetY, targetZ);
            this.player = entity;
            this.pearlEntity = pearlEntity;
            this.attackDamage = attackDamage;
        }

        public net.minecraft.world.entity.projectile.ThrownEnderpearl getPearlEntity() {
            return pearlEntity;
        }

        public float getAttackDamage() {
            return attackDamage;
        }

        public void setAttackDamage(float attackDamage) {
            this.attackDamage = attackDamage;
        }
    }
}
