package com.grandsigil.client;

import com.grandsigil.entity.BoltEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class BoltParticles {
   private static final DustParticleOptions CHARGE = new DustParticleOptions(new Vector3f(0.62F, 0.82F, 1.0F), 1.0F);
   private static final DustParticleOptions DUST = new DustParticleOptions(new Vector3f(0.55F, 0.55F, 0.62F), 1.2F);

   private BoltParticles() {
   }

   public static void ambient(BoltEntity bolt) {
      Level level = bolt.level();
      RandomSource random = level.random;
      Vec3 at = bolt.position();
      float radius = bolt.radius();
      switch (bolt.stage()) {
         case 0:
            gathering(level, random, at, radius, (float)bolt.stageAge() / 60.0F);
         case 1:
            break;
         default:
            after(level, random, at, radius, bolt.stageAge());
      }
   }

   private static void gathering(Level level, RandomSource random, Vec3 at, float radius, float progress) {
      int count = 2 + Mth.floor(progress * 16.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            CHARGE, at.x + Math.cos(a) * rr, at.y + 0.15, at.z + Math.sin(a) * rr, 0.0, 0.08 + random.nextDouble() * 0.35 * (double)progress, 0.0
         );
      }

      int dust = Mth.floor(progress * 8.0F);

      for (int i = 0; i < dust; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(DUST, at.x + Math.cos(a) * rr, at.y + 0.1, at.z + Math.sin(a) * rr, 0.0, 0.05 + random.nextDouble() * 0.2, 0.0);
      }
   }

   private static void after(Level level, RandomSource random, Vec3 at, float radius, int age) {
      if (age <= 70) {
         float fade = 1.0F - (float)age / 70.0F;
         int count = 4 + Mth.floor(fade * fade * 40.0F);

         for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius;
            level.addParticle(
               (ParticleOptions)(random.nextInt(3) == 0 ? ParticleTypes.END_ROD : CHARGE),
               at.x + Math.cos(a) * rr,
               at.y + 0.2 + random.nextDouble() * 3.0 * (double)fade,
               at.z + Math.sin(a) * rr,
               (random.nextDouble() - 0.5) * 0.3,
               random.nextDouble() * 0.4,
               (random.nextDouble() - 0.5) * 0.3
            );
         }
      }
   }
}
