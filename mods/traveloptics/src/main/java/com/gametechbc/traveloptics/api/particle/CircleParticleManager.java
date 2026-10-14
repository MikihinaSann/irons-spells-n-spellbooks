package com.gametechbc.traveloptics.api.particle;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class CircleParticleManager {
   private static final Random RANDOM = new Random();

   public CircleParticleManager() {
      super();
   }

   public static void spawnParticles(
      Level level, LivingEntity entity, int particleCount, ParticleOptions particleType, ParticleDirection direction, double parameter
   ) {
      if (!level.isClientSide) {
         double centerY = entity.getY();

         for (int i = 0; i < particleCount; i++) {
            double theta = (Math.PI * 2) * RANDOM.nextDouble();
            double xOffset = Math.cos(theta);
            double zOffset = Math.sin(theta);
            double yOffset = 0.0;
            if (direction == ParticleDirection.INWARD) {
               xOffset *= parameter;
               zOffset *= parameter;
               Vec3 directionVector = new Vec3(entity.getX() - (entity.getX() + xOffset), 0.0, entity.getZ() - (entity.getZ() + zOffset)).normalize();
               MagicManager.spawnParticles(
                  level, particleType, entity.getX() + xOffset, centerY, entity.getZ() + zOffset, 0, directionVector.x, 0.0, directionVector.z, 0.1, true
               );
            } else {
               Vec3 directionVector = new Vec3(xOffset, yOffset, zOffset).normalize().scale(parameter);
               MagicManager.spawnParticles(level, particleType, entity.getX(), centerY, entity.getZ(), 0, directionVector.x, 0.0, directionVector.z, 0.1, true);
            }
         }
      }
   }
}
