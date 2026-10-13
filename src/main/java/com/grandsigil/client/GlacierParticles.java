package com.grandsigil.client;

import com.grandsigil.entity.GlacierEntity;
import com.grandsigil.fx.IronParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GlacierParticles {
   private static final DustParticleOptions RIME = new DustParticleOptions(new Vector3f(0.8F, 0.94F, 1.0F), 1.2F);
   private static final DustParticleOptions DEEP = new DustParticleOptions(new Vector3f(0.32F, 0.62F, 0.82F), 1.6F);

   private GlacierParticles() {
   }

   public static void ambient(GlacierEntity glacier) {
      Level level = glacier.level();
      RandomSource random = level.random;
      Vec3 at = glacier.position();
      float radius = glacier.radius();
      switch (glacier.stage()) {
         case 0:
            break;
         case 1:
            rising(level, random, at, radius, glacier.falling(0.0F));
            break;
         case 2:
            holding(level, random, at, radius);
            break;
         default:
            breaking(level, random, at, radius, glacier.breaking(0.0F));
      }
   }

   private static void rising(Level level, RandomSource random, Vec3 at, float radius, float risen) {
      int count = 8 + Math.round(risen * 14.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            IronParticles.or("irons_spellbooks:snow_dust", RIME),
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * 3.0,
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.1,
            0.18 + random.nextDouble() * 0.24,
            (random.nextDouble() - 0.5) * 0.1
         );
      }
   }

   private static void holding(Level level, RandomSource random, Vec3 at, float radius) {
      for (int i = 0; i < 12; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            (ParticleOptions)(random.nextInt(3) == 0 ? DEEP : IronParticles.or("irons_spellbooks:snowflake", RIME)),
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * 42.0,
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.03,
            0.06 + random.nextDouble() * 0.1,
            (random.nextDouble() - 0.5) * 0.03
         );
      }

      if (random.nextInt(2) == 0) {
         double a = random.nextDouble() * Math.PI * 2.0;
         level.addParticle(RIME, at.x + Math.cos(a) * (double)radius, at.y + 0.4, at.z + Math.sin(a) * (double)radius, 0.0, 0.04, 0.0);
      }
   }

   private static void breaking(Level level, RandomSource random, Vec3 at, float radius, float broken) {
      int count = Math.round((1.0F - broken) * 14.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            RIME,
            at.x + Math.cos(a) * rr,
            at.y + 1.0 + random.nextDouble() * 42.0 * (double)(1.0F - broken),
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.02,
            0.02 + random.nextDouble() * 0.03,
            (random.nextDouble() - 0.5) * 0.02
         );
      }

      if (random.nextInt(2) == 0) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(ParticleTypes.FALLING_WATER, at.x + Math.cos(a) * rr, at.y + 0.6, at.z + Math.sin(a) * rr, 0.0, 0.0, 0.0);
      }
   }
}
