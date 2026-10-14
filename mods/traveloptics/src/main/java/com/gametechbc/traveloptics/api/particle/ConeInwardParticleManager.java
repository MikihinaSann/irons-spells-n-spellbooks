package com.gametechbc.traveloptics.api.particle;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ConeInwardParticleManager {
   private static final Random RANDOM = new Random();

   public ConeInwardParticleManager() {
      super();
   }

   public static void spawnInwardConeParticles(
      Level level, LivingEntity entity, double distance, int particleCount, double xOffset, double yOffset, double zOffset, ParticleOptions particleType
   ) {
      if (!level.isClientSide) {
         Vec3 lookVec = entity.getLookAngle().normalize();
         double yOffsetAdjustment = entity.getBbHeight() * 0.5 + yOffset;

         for (int i = 0; i < particleCount; i++) {
            double angle = RANDOM.nextDouble() * 2.0 * Math.PI;
            double heightFactor = RANDOM.nextDouble();
            double radius = (1.0 - heightFactor) * 1.5;
            double xConeOffset = radius * Math.cos(angle);
            double zConeOffset = radius * Math.sin(angle);
            double yConeOffset = (heightFactor - 0.5) * 2.0;
            Vec3 particleBasePos = entity.position()
               .add(lookVec.scale(distance))
               .add(new Vec3(xConeOffset + xOffset, yConeOffset + yOffsetAdjustment, zConeOffset + zOffset));
            Vec3 casterPosAdjusted = entity.position().add(0.0, yOffsetAdjustment, 0.0);
            Vec3 directionVec = casterPosAdjusted.subtract(particleBasePos).normalize();
            MagicManager.spawnParticles(
               level, particleType, particleBasePos.x, particleBasePos.y, particleBasePos.z, 0, directionVec.x, directionVec.y, directionVec.z, 0.1, true
            );
         }
      }
   }
}
