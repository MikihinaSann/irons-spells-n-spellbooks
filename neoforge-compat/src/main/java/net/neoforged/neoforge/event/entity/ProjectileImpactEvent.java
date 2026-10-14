package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when a projectile impacts something. Canceling prevents the vanilla
 * {@code Projectile#onHit} handling. Ported from NeoForge 21.1.
 */
public class ProjectileImpactEvent extends EntityEvent implements ICancellableEvent {
    private final HitResult ray;
    private final Projectile projectile;

    public ProjectileImpactEvent(Projectile projectile, HitResult ray) {
        super(projectile);
        this.ray = ray;
        this.projectile = projectile;
    }

    public HitResult getRayTraceResult() {
        return ray;
    }

    public Projectile getProjectile() {
        return projectile;
    }
}
