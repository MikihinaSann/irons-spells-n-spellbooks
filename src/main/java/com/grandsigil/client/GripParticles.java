package com.grandsigil.client;

import com.grandsigil.entity.GripEntity;
import com.grandsigil.fx.IronParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GripParticles {
   private static final DustParticleOptions MOTE = new DustParticleOptions(new Vector3f(0.6F, 0.22F, 0.9F), 1.4F);
   private static final DustParticleOptions GRIT = new DustParticleOptions(new Vector3f(0.34F, 0.3F, 0.26F), 1.0F);

   private GripParticles() {
   }

   public static void ambient(GripEntity grip) {
      Level level = grip.level();
      RandomSource random = level.random;
      Vec3 at = grip.position();
      float radius = grip.radius();
      switch (grip.stage()) {
         case 0:
            seizing(level, random, at, radius, grip.seized(0.0F));
            break;
         case 3:
            slamming(level, random, at, radius, grip.slam(0.0F));
            break;
         default:
            holding(level, random, at, radius, grip.risen(0.0F));
      }
   }

   private static void seizing(Level level, RandomSource random, Vec3 at, float radius, float here) {
      int count = 6 + Math.round(here * 22.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            random.nextInt(3) == 0 ? MOTE : GRIT,
            at.x + Math.cos(a) * rr,
            at.y + 0.2 + random.nextDouble() * 2.0,
            at.z + Math.sin(a) * rr,
            0.0,
            0.1 + random.nextDouble() * 0.22,
            0.0
         );
      }
   }

   private static void holding(Level level, RandomSource random, Vec3 at, float radius, float risen) {
      for (int i = 0; i < 26; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            (ParticleOptions)(random.nextInt(4) == 0 ? IronParticles.or("irons_spellbooks:unstable_ender", MOTE) : GRIT),
            at.x + Math.cos(a) * rr,
            at.y + 0.3 + random.nextDouble() * 30.0 * (double)risen,
            at.z + Math.sin(a) * rr,
            0.0,
            0.16 + random.nextDouble() * 0.3,
            0.0
         );
      }

      if (random.nextInt(2) == 0) {
         double a = random.nextDouble() * Math.PI * 2.0;
         level.addParticle(MOTE, at.x + Math.cos(a) * (double)radius, at.y + 0.5, at.z + Math.sin(a) * (double)radius, 0.0, 0.09, 0.0);
      }
   }

   private static void slamming(Level level, RandomSource random, Vec3 at, float radius, float slam) {
      if (!(slam < 0.25F)) {
         int count = Math.round((1.0F - slam) * 44.0F);

         for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius * (double)slam;
            level.addParticle(
               (ParticleOptions)(random.nextInt(3) == 0 ? ParticleTypes.POOF : GRIT),
               at.x + Math.cos(a) * rr,
               at.y + 0.3 + random.nextDouble() * 1.6,
               at.z + Math.sin(a) * rr,
               Math.cos(a) * 0.35,
               0.04,
               Math.sin(a) * 0.35
            );
         }
      } else {
         for (int i = 0; i < 40; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius;
            level.addParticle(
               IronParticles.or("irons_spellbooks:unstable_ender", MOTE),
               at.x + Math.cos(a) * rr,
               at.y + random.nextDouble() * 30.0,
               at.z + Math.sin(a) * rr,
               0.0,
               -1.4 - random.nextDouble() * 0.8,
               0.0
            );
         }
      }
   }
}
