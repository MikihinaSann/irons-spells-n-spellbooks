package com.grandsigil.client;

import com.grandsigil.entity.SunEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class SunParticles {
   private static final DustParticleOptions EMBER = new DustParticleOptions(new Vector3f(1.0F, 0.55F, 0.14F), 1.1F);
   private static final DustParticleOptions ASH = new DustParticleOptions(new Vector3f(0.28F, 0.24F, 0.22F), 1.4F);

   private SunParticles() {
   }

   public static void ambient(SunEntity sun) {
      Level level = sun.level();
      RandomSource random = level.random;
      Vec3 at = sun.position();
      float radius = sun.radius();
      float age = (float)sun.stageAge();
      switch (sun.stage()) {
         case 0:
            heat(level, random, at, radius, age / 80.0F * 0.35F);
            break;
         case 1:
            heat(level, random, at, radius, 0.35F + 0.65F * age / 60.0F);
            break;
         case 2:
            firing(level, random, at, radius);
            break;
         default:
            burning(level, random, at, radius, sun.stageAge());
      }
   }

   private static void heat(Level level, RandomSource random, Vec3 at, float radius, float heat) {
      int count = 2 + Mth.floor(heat * 16.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(EMBER, at.x + Math.cos(a) * rr, at.y + 0.1, at.z + Math.sin(a) * rr, 0.0, 0.05 + random.nextDouble() * 0.16 * (double)heat, 0.0);
      }

      if (random.nextFloat() < heat) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(ParticleTypes.SMOKE, at.x + Math.cos(a) * rr, at.y + 0.4, at.z + Math.sin(a) * rr, 0.0, 0.06, 0.0);
      }

      if (heat > 0.5F && random.nextInt(2) == 0) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(ASH, at.x + Math.cos(a) * rr, at.y + 14.0 + random.nextDouble() * 12.0, at.z + Math.sin(a) * rr, 0.0, -0.3, 0.0);
      }
   }

   private static void firing(Level level, RandomSource random, Vec3 at, float radius) {
      for (int i = 0; i < 30; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius * 0.8;
         level.addParticle(
            (ParticleOptions)(random.nextInt(3) == 0 ? ParticleTypes.END_ROD : EMBER),
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * 28.0,
            at.z + Math.sin(a) * rr,
            0.0,
            0.9 + random.nextDouble() * 1.2,
            0.0
         );
      }
   }

   private static void burning(Level level, RandomSource random, Vec3 at, float radius, int age) {
      if (age <= 55) {
         float fade = 1.0F - (float)age / 55.0F;
         int count = 4 + Mth.floor(fade * 14.0F);

         for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius;
            level.addParticle(
               random.nextBoolean() ? EMBER : ASH, at.x + Math.cos(a) * rr, at.y + 0.3, at.z + Math.sin(a) * rr, 0.0, 0.25 + random.nextDouble() * 0.5, 0.0
            );
         }

         for (int i = 0; i < 4; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius * 0.5;
            level.addParticle(ParticleTypes.LARGE_SMOKE, at.x + Math.cos(a) * rr, at.y + 1.0, at.z + Math.sin(a) * rr, 0.0, 0.35, 0.0);
         }
      }
   }
}
