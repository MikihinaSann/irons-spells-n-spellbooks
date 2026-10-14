package com.gametechbc.traveloptics.api.particle;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AdvancedCylinderParticleManager {
   private static final Random RANDOM = new Random();

   public AdvancedCylinderParticleManager() {
      super();
   }

   public static void spawnParticles(
      Level level,
      Vec3 position,
      int particleCount,
      ParticleOptions particleType,
      ParticleDirection direction,
      double radius,
      double height,
      double xOffset,
      double yOffset,
      double zOffset,
      double speed,
      boolean force
   ) {
      if (!level.isClientSide) {
         double baseX = position.x() + xOffset;
         double baseY = position.y() + yOffset;
         double baseZ = position.z() + zOffset;

         for (int i = 0; i < particleCount; i++) {
            double theta = (Math.PI * 2) * RANDOM.nextDouble();
            double yPosition = baseY + RANDOM.nextDouble() * height;
            double xParticleOffset = radius * Math.cos(theta);
            double zParticleOffset = radius * Math.sin(theta);
            Vec3 directionVector;
            if (direction == ParticleDirection.UPWARD) {
               directionVector = new Vec3(0.0, 1.0, 0.0).normalize();
            } else if (direction == ParticleDirection.DOWNWARD) {
               directionVector = new Vec3(0.0, -1.0, 0.0).normalize();
            } else if (direction == ParticleDirection.INWARD) {
               directionVector = new Vec3(-xParticleOffset, 0.0, -zParticleOffset).normalize();
            } else {
               directionVector = new Vec3(xParticleOffset, 0.0, zParticleOffset).normalize();
            }

            directionVector = directionVector.scale(speed);
            MagicManager.spawnParticles(
               level,
               particleType,
               baseX + xParticleOffset,
               yPosition,
               baseZ + zParticleOffset,
               0,
               directionVector.x,
               directionVector.y,
               directionVector.z,
               0.1,
               force
            );
         }
      }
   }
}
