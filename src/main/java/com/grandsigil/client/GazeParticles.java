package com.grandsigil.client;

import com.grandsigil.entity.GazeEntity;
import com.grandsigil.fx.IronParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GazeParticles {
   private static final DustParticleOptions VOID = new DustParticleOptions(new Vector3f(0.42F, 0.16F, 0.72F), 1.6F);
   private static final DustParticleOptions ASH = new DustParticleOptions(new Vector3f(0.28F, 0.26F, 0.3F), 1.2F);

   private GazeParticles() {
   }

   public static void ambient(GazeEntity gaze) {
      Level level = gaze.level();
      RandomSource random = level.random;
      Vec3 at = gaze.position();
      float radius = gaze.radius();
      float open = gaze.open(0.0F);
      switch (gaze.stage()) {
         case 0:
            opening(level, random, at, radius, open);
            break;
         case 1:
            watching(level, random, at, radius, gaze);
            break;
         default:
            closing(level, random, at, radius, open);
      }
   }

   private static void opening(Level level, RandomSource random, Vec3 at, float radius, float open) {
      int count = 3 + Math.round(open * 9.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            random.nextInt(3) == 0 ? VOID : ASH,
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * 4.0,
            at.z + Math.sin(a) * rr,
            0.0,
            0.08 + random.nextDouble() * 0.12,
            0.0
         );
      }
   }

   private static void watching(Level level, RandomSource random, Vec3 at, float radius, GazeEntity gaze) {
      for (int i = 0; i < 4; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            IronParticles.or("irons_spellbooks:fog", ASH), at.x + Math.cos(a) * rr, at.y + random.nextDouble() * 6.0, at.z + Math.sin(a) * rr, 0.0, 0.03, 0.0
         );
      }

      if (random.nextInt(2) == 0) {
         double a = random.nextDouble() * Math.PI * 2.0;
         level.addParticle(VOID, at.x + Math.cos(a) * (double)radius, at.y + 0.5, at.z + Math.sin(a) * (double)radius, 0.0, 0.05, 0.0);
      }
   }

   private static void closing(Level level, RandomSource random, Vec3 at, float radius, float open) {
      int count = Math.round(open * 8.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            (ParticleOptions)(random.nextInt(4) == 0 ? ParticleTypes.PORTAL : ASH),
            at.x + Math.cos(a) * rr,
            at.y + 2.0 + random.nextDouble() * 12.0,
            at.z + Math.sin(a) * rr,
            0.0,
            -0.04 - random.nextDouble() * 0.06,
            0.0
         );
      }
   }
}
