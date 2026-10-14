package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Reproduces the NeoForge projectile-impact patch on every vanilla call site of
 * {@code hitTargetOrDeflectSelf}. Vanilla {@code AbstractArrow#tick} and
 * {@code FireworkRocketEntity#tick} invoke it even for {@code MISS} ray results
 * (the clip call never returns null), while NeoForge only lets real hits through
 * and fires {@code ProjectileImpactEvent} first. Modded projectiles such as
 * Cataclysm's Ceraunus treat any {@code onHit} call as a real impact, so the
 * missing MISS guard made them bounce back instantly.
 */
public final class ProjectileImpactDispatchMixin {
    private ProjectileImpactDispatchMixin() {
    }

    @Mixin(AbstractArrow.class)
    public abstract static class AbstractArrowDispatch {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "hitTargetOrDeflectSelf"))
        private ProjectileDeflection irons_spellbooks$impactGuard(AbstractArrow self, HitResult hitResult,
                                                                  Operation<ProjectileDeflection> original) {
            if (hitResult.getType() == HitResult.Type.MISS || EventHooks.onProjectileImpact(self, hitResult)) {
                return ProjectileDeflection.NONE;
            }
            return original.call(self, hitResult);
        }
    }

    @Mixin(AbstractHurtingProjectile.class)
    public abstract static class HurtingProjectileDispatch {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "hitTargetOrDeflectSelf"))
        private ProjectileDeflection irons_spellbooks$impactGuard(AbstractHurtingProjectile self, HitResult hitResult,
                                                                  Operation<ProjectileDeflection> original) {
            if (hitResult.getType() == HitResult.Type.MISS || EventHooks.onProjectileImpact(self, hitResult)) {
                return ProjectileDeflection.NONE;
            }
            return original.call(self, hitResult);
        }
    }

    @Mixin(ThrowableProjectile.class)
    public abstract static class ThrowableProjectileDispatch {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "hitTargetOrDeflectSelf"))
        private ProjectileDeflection irons_spellbooks$impactGuard(ThrowableProjectile self, HitResult hitResult,
                                                                  Operation<ProjectileDeflection> original) {
            if (hitResult.getType() == HitResult.Type.MISS || EventHooks.onProjectileImpact(self, hitResult)) {
                return ProjectileDeflection.NONE;
            }
            return original.call(self, hitResult);
        }
    }

    @Mixin(ShulkerBullet.class)
    public abstract static class ShulkerBulletDispatch {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "hitTargetOrDeflectSelf"))
        private ProjectileDeflection irons_spellbooks$impactGuard(ShulkerBullet self, HitResult hitResult,
                                                                  Operation<ProjectileDeflection> original) {
            if (hitResult.getType() == HitResult.Type.MISS || EventHooks.onProjectileImpact(self, hitResult)) {
                return ProjectileDeflection.NONE;
            }
            return original.call(self, hitResult);
        }
    }

    @Mixin(LlamaSpit.class)
    public abstract static class LlamaSpitDispatch {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "hitTargetOrDeflectSelf"))
        private ProjectileDeflection irons_spellbooks$impactGuard(LlamaSpit self, HitResult hitResult,
                                                                  Operation<ProjectileDeflection> original) {
            if (hitResult.getType() == HitResult.Type.MISS || EventHooks.onProjectileImpact(self, hitResult)) {
                return ProjectileDeflection.NONE;
            }
            return original.call(self, hitResult);
        }
    }

    @Mixin(FireworkRocketEntity.class)
    public abstract static class FireworkRocketDispatch {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "hitTargetOrDeflectSelf"))
        private ProjectileDeflection irons_spellbooks$impactGuard(FireworkRocketEntity self, HitResult hitResult,
                                                                  Operation<ProjectileDeflection> original) {
            if (hitResult.getType() == HitResult.Type.MISS || EventHooks.onProjectileImpact(self, hitResult)) {
                return ProjectileDeflection.NONE;
            }
            return original.call(self, hitResult);
        }
    }
}
