package com.somake.somakespells.effect.blood;

import com.somake.somakespells.registries.ModParticles;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RoseSecretEffect extends MagicMobEffect implements ISyncedMobEffect {
   private static final int PETALS_PER_TICK = 2;

   public RoseSecretEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      if (livingEntity.level().isClientSide) {
         for (int i = 0; i < 2; i++) {
            double angle = livingEntity.getRandom().nextDouble() * (float) (Math.PI * 2);
            double radius = 0.15 + livingEntity.getRandom().nextDouble() * Math.max(0.25, livingEntity.getBbWidth() * 0.35);
            double x = livingEntity.getX() + Math.cos(angle) * radius;
            double y = livingEntity.getY() + 0.6 + livingEntity.getRandom().nextDouble() * Math.max(0.8, livingEntity.getBbHeight() * 0.45);
            double z = livingEntity.getZ() + Math.sin(angle) * radius;
            Vec3 velocity = new Vec3(
               (livingEntity.getRandom().nextDouble() - 0.5) * 0.02,
               -0.02 - livingEntity.getRandom().nextDouble() * 0.015,
               (livingEntity.getRandom().nextDouble() - 0.5) * 0.02
            );
            livingEntity.level().addParticle((ParticleOptions)ModParticles.WITHERED_ROSE_PETAL.get(), x, y, z, velocity.x, velocity.y, velocity.z);
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
