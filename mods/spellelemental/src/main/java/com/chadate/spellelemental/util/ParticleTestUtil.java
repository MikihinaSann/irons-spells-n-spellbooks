package com.chadate.spellelemental.util;

import com.chadate.spellelemental.register.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ParticleTestUtil {
   public static void spawnPhysicalResistanceDownParticles(LivingEntity entity, int count) {
      Level level = entity.level();
      if (level instanceof ServerLevel serverLevel) {
         double x = entity.getX();
         double y = entity.getY() + entity.getBbHeight() / 2.0;
         double z = entity.getZ();

         for (int i = 0; i < count; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetY = level.random.nextDouble() * entity.getBbHeight();
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;
            double velocityX = (level.random.nextDouble() - 0.5) * 0.1;
            double velocityY = -0.05 - level.random.nextDouble() * 0.05;
            double velocityZ = (level.random.nextDouble() - 0.5) * 0.1;
            serverLevel.sendParticles(ModParticles.PHYSICAL_RESISTANCE_DOWN.get(), x + offsetX, y + offsetY, z + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }
   }

   public static void spawnPhysicalResistanceDownBurst(Level level, double x, double y, double z, int intensity) {
      if (level instanceof ServerLevel serverLevel) {
         int particleCount = 10 * intensity;

         for (int i = 0; i < particleCount; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double radius = level.random.nextDouble() * 1.5;
            double height = level.random.nextDouble() * 2.0 - 1.0;
            double particleX = x + Math.cos(angle) * radius;
            double particleY = y + height;
            double particleZ = z + Math.sin(angle) * radius;
            double velocityX = Math.cos(angle) * 0.1;
            double velocityY = -0.1 + level.random.nextDouble() * 0.05;
            double velocityZ = Math.sin(angle) * 0.1;
            serverLevel.sendParticles(ModParticles.PHYSICAL_RESISTANCE_DOWN.get(), particleX, particleY, particleZ, 1, velocityX, velocityY, velocityZ, 0.1);
         }
      }
   }
}
