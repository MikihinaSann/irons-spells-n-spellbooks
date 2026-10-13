package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.ICancellableEvent;

public class ProjectileImpactEvent extends EntityEvent implements ICancellableEvent {
    private final HitResult rayTraceResult;
    private final Projectile projectile;
    private ImpactRule impactRule = ImpactRule.DEFAULT;

    public enum ImpactRule {
        DEFAULT,
        SKIP_ENTITY,
        SKIP_BLOCK,
        SKIP_ALL
    }

    public ProjectileImpactEvent(Projectile projectile, HitResult ray) {
        super(projectile);
        this.projectile = projectile;
        this.rayTraceResult = ray;
    }

    public HitResult getRayTraceResult() {
        return rayTraceResult;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public ImpactRule getImpactRule() {
        return impactRule;
    }

    public void setImpactRule(ImpactRule rule) {
        this.impactRule = rule;
    }
}
