package net.neoforged.neoforge.event.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;

/**
 * Fired when an explosion applies knockback to an entity; the velocity is mutable.
 */
public class ExplosionKnockbackEvent extends Event {
    private final Level level;
    private final Explosion explosion;
    private final Entity entity;
    private Vec3 knockbackVelocity;

    public ExplosionKnockbackEvent(Level level, Explosion explosion, Entity entity, Vec3 knockbackVelocity) {
        this.level = level;
        this.explosion = explosion;
        this.entity = entity;
        this.knockbackVelocity = knockbackVelocity;
    }

    public Level getLevel() { return level; }
    public Explosion getExplosion() { return explosion; }
    public Entity getEntity() { return entity; }
    public Vec3 getKnockbackVelocity() { return knockbackVelocity; }
    public void setKnockbackVelocity(Vec3 knockbackVelocity) { this.knockbackVelocity = knockbackVelocity; }
}
