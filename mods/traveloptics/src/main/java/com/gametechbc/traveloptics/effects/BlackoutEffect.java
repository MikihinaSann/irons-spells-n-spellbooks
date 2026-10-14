package com.gametechbc.traveloptics.effects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BlackoutEffect extends MobEffect {
   public BlackoutEffect() {
      super(MobEffectCategory.HARMFUL, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false, false));
      if (!entity.level().isClientSide() && entity.level() instanceof ServerLevel serverLevel) {
         int particleCount = 3;

         for (int i = 0; i < particleCount; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
            double velocityX = (entity.getRandom().nextDouble() - 0.5) * 0.2;
            double velocityY = (entity.getRandom().nextDouble() - 0.5) * 0.2;
            double velocityZ = (entity.getRandom().nextDouble() - 0.5) * 0.2;
            serverLevel.sendParticles(
               ParticleTypes.SMOKE, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, 1, velocityX, velocityY, velocityZ, 0.0
            );
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
