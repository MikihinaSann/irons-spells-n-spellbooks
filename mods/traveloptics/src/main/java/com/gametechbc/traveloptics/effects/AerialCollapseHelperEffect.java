package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOEffects;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class AerialCollapseHelperEffect extends MobEffect {
   public AerialCollapseHelperEffect() {
      super(MobEffectCategory.BENEFICIAL, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      MobEffectInstance effectInstance = entity.getEffect(TOEffects.AERIAL_COLLAPSE_HELPER);
      if (effectInstance != null) {
         int duration = effectInstance.getDuration();
         if (duration > 60) {
            entity.removeEffect(TOEffects.AERIAL_COLLAPSE_HELPER);
            entity.addEffect(new MobEffectInstance(TOEffects.AERIAL_COLLAPSE_HELPER, 60, amplifier, false, true));
         }

         if (duration == 5 && !entity.level().isClientSide) {
            CameraShakeManager.addCameraShake(new CameraShakeData(20, entity.position(), 8.0F));
         }
      }

      if (!entity.level().isClientSide) {
         ParticleOptions[] particleTypes = new ParticleOptions[]{ParticleHelper.ACID, ParticleHelper.ACID_BUBBLE};
         int[] spawnPercentages = new int[]{10, 90};
         int totalParticles = 8;

         for (int i = 0; i < totalParticles; i++) {
            int randomValue = entity.getRandom().nextInt(100);
            ParticleOptions selectedParticle = null;
            int cumulativePercentage = 0;

            for (int j = 0; j < particleTypes.length; j++) {
               cumulativePercentage += spawnPercentages[j];
               if (randomValue < cumulativePercentage) {
                  selectedParticle = particleTypes[j];
                  break;
               }
            }

            if (selectedParticle != null) {
               AABB boundingBox = entity.getBoundingBox();
               double x = boundingBox.minX + (boundingBox.maxX - boundingBox.minX) * entity.getRandom().nextDouble();
               double y = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * entity.getRandom().nextDouble();
               double z = boundingBox.minZ + (boundingBox.maxZ - boundingBox.minZ) * entity.getRandom().nextDouble();
               double motionX = 0.0;
               double motionY = -0.4 - entity.getRandom().nextDouble() * 0.2;
               double motionZ = 0.0;
               MagicManager.spawnParticles(
                  entity.level(), selectedParticle, entity.getX(), entity.getY(), entity.getZ(), 1, motionX, motionY, motionZ, 0.03, false
               );
            }
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
