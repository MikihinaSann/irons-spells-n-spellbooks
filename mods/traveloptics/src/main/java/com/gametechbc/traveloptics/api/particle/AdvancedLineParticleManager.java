package com.gametechbc.traveloptics.api.particle;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.Random;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AdvancedLineParticleManager {
   private static final Random RANDOM = new Random();

   public AdvancedLineParticleManager() {
      super();
   }

   public static void spawnParticles(
      Level level,
      Entity entity,
      int particleCount,
      ParticleOptions particleType,
      ParticleDirection direction,
      double length,
      double height,
      double frontOffset,
      double speed,
      boolean force
   ) {
      if (!level.isClientSide) {
         Vec3 lookVector = entity.getLookAngle().normalize();
         Vec3 rightVector = new Vec3(-lookVector.z, 0.0, lookVector.x).normalize();
         Vec3 startPosition = entity.position().add(lookVector.scale(frontOffset));
         double halfLength = length / 2.0;
         Vec3 lineStart = startPosition.subtract(rightVector.scale(halfLength));
         Vec3 lineEnd = startPosition.add(rightVector.scale(halfLength));

         for (int i = 0; i < particleCount; i++) {
            double t = RANDOM.nextDouble();
            Vec3 spawnPos = lineStart.lerp(lineEnd, t);
            double yPosition = spawnPos.y + RANDOM.nextDouble() * height;
            Vec3 directionVector;
            if (direction == ParticleDirection.UPWARD) {
               directionVector = new Vec3(0.0, 1.0, 0.0).normalize();
            } else if (direction == ParticleDirection.DOWNWARD) {
               directionVector = new Vec3(0.0, -1.0, 0.0).normalize();
            } else {
               directionVector = new Vec3(0.0, 0.0, 0.0);
            }

            directionVector = directionVector.scale(speed);
            MagicManager.spawnParticles(
               level, particleType, spawnPos.x, yPosition, spawnPos.z, 0, directionVector.x, directionVector.y, directionVector.z, 0.1, force
            );
         }
      }
   }
}
