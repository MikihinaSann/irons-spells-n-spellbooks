package com.grandsigil.client;

import com.grandsigil.entity.GateEntity;
import com.grandsigil.fx.IronParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GateParticles {
   private static final DustParticleOptions MOTE = new DustParticleOptions(new Vector3f(1.0F, 0.94F, 0.72F), 1.0F);
   private static final DustParticleOptions FEATHER = new DustParticleOptions(new Vector3f(1.0F, 0.98F, 0.9F), 1.5F);

   private GateParticles() {
   }

   public static void ambient(GateEntity gate) {
      Level level = gate.level();
      RandomSource random = level.random;
      Vec3 at = gate.position();
      float radius = gate.radius();
      float open = gate.opening(0.0F);
      switch (gate.stage()) {
         case 0:
            opening(level, random, at, radius, open);
            break;
         case 1:
            holding(level, random, at, radius);
            break;
         default:
            closing(level, random, at, radius, open);
      }
   }

   private static void opening(Level level, RandomSource random, Vec3 at, float radius, float open) {
      int count = 2 + Math.round(open * 10.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = 12.0 * (0.4 + random.nextDouble() * 0.9);
         level.addParticle(
            IronParticles.or("irons_spellbooks:wisp", MOTE),
            at.x + Math.cos(a) * rr,
            at.y + 120.0 * (0.55 + random.nextDouble() * 0.4),
            at.z + Math.sin(a) * rr,
            0.0,
            -0.15 - random.nextDouble() * 0.2,
            0.0
         );
      }
   }

   private static void holding(Level level, RandomSource random, Vec3 at, float radius) {
      for (int i = 0; i < 10; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * 12.0;
         level.addParticle(
            (ParticleOptions)(random.nextInt(4) == 0 ? FEATHER : IronParticles.or("irons_spellbooks:falling_sparkle", MOTE)),
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * 34.0,
            at.z + Math.sin(a) * rr,
            0.0,
            -0.1 - random.nextDouble() * 0.14,
            0.0
         );
      }

      for (int i = 0; i < 3; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(MOTE, at.x + Math.cos(a) * rr, at.y + 1.0 + random.nextDouble() * 10.0, at.z + Math.sin(a) * rr, 0.0, -0.05, 0.0);
      }

      if (random.nextInt(3) == 0) {
         double a = random.nextDouble() * Math.PI * 2.0;
         level.addParticle(
            IronParticles.or("irons_spellbooks:cleanse", ParticleTypes.END_ROD),
            at.x + Math.cos(a) * (double)radius,
            at.y + 0.4,
            at.z + Math.sin(a) * (double)radius,
            0.0,
            0.03,
            0.0
         );
      }
   }

   private static void closing(Level level, RandomSource random, Vec3 at, float radius, float open) {
      int count = 4 + Math.round(open * 8.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            IronParticles.or("irons_spellbooks:cleanse", FEATHER),
            at.x + Math.cos(a) * rr,
            at.y + 4.0 + random.nextDouble() * 22.0,
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.04,
            -0.06,
            (random.nextDouble() - 0.5) * 0.04
         );
      }
   }
}
