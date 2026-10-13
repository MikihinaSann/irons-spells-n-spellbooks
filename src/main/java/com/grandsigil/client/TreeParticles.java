package com.grandsigil.client;

import com.grandsigil.entity.TreeEntity;
import com.grandsigil.fx.IronParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class TreeParticles {
   private static final DustParticleOptions SAP = new DustParticleOptions(new Vector3f(0.75F, 1.0F, 0.45F), 1.0F);
   private static final DustParticleOptions LEAF = new DustParticleOptions(new Vector3f(0.42F, 0.85F, 0.35F), 1.3F);
   private static final DustParticleOptions GOLD = new DustParticleOptions(new Vector3f(1.0F, 0.88F, 0.42F), 1.1F);

   private TreeParticles() {
   }

   public static void ambient(TreeEntity tree) {
      Level level = tree.level();
      RandomSource random = level.random;
      Vec3 at = tree.position();
      float radius = tree.radius();
      float height = tree.height() * tree.risen(0.0F);
      int generation = tree.generation();
      switch (tree.stage()) {
         case 0:
            growing(level, random, at, radius, height);
            break;
         case 1:
            standing(level, random, at, radius * tree.rootReach(0.0F), height, generation);
            break;
         default:
            blooming(level, random, at, radius, height, generation);
      }
   }

   private static void growing(Level level, RandomSource random, Vec3 at, float radius, float height) {
      for (int i = 0; i < 20; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius * 0.6;
         level.addParticle(
            random.nextBoolean() ? SAP : LEAF,
            at.x + Math.cos(a) * rr,
            at.y + 0.2,
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.3,
            0.25 + random.nextDouble() * 0.5,
            (random.nextDouble() - 0.5) * 0.3
         );
      }
   }

   private static void standing(Level level, RandomSource random, Vec3 at, float radius, float height, int generation) {
      for (int i = 0; i < 3; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = 1.0 + random.nextDouble() * 1.6;
         level.addParticle(
            generation >= 4 ? GOLD : SAP,
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * (double)height * 0.5,
            at.z + Math.sin(a) * rr,
            0.0,
            0.25 + random.nextDouble() * 0.3,
            0.0
         );
      }

      int falling = 3 + generation;

      for (int i = 0; i < falling; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius * 0.75;
         level.addParticle(
            LEAF,
            at.x + Math.cos(a) * rr,
            at.y + (double)height * (0.55 + random.nextDouble() * 0.4),
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.08,
            -0.08,
            (random.nextDouble() - 0.5) * 0.08
         );
      }

      int flies = 2 + generation;

      for (int i = 0; i < flies; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius * 0.7;
         level.addParticle(
            IronParticles.or("irons_spellbooks:firefly", ParticleTypes.HAPPY_VILLAGER),
            at.x + Math.cos(a) * rr,
            at.y + (double)height * (0.5 + random.nextDouble() * 0.45),
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.02,
            0.01,
            (random.nextDouble() - 0.5) * 0.02
         );
      }

      for (int i = 0; i < 2; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = (double)radius * (0.85 + random.nextDouble() * 0.15);
         level.addParticle(
            IronParticles.or("irons_spellbooks:acid", ParticleTypes.HAPPY_VILLAGER),
            at.x + Math.cos(a) * rr,
            at.y + 0.3,
            at.z + Math.sin(a) * rr,
            0.0,
            0.05,
            0.0
         );
      }
   }

   private static void blooming(Level level, RandomSource random, Vec3 at, float radius, float height, int generation) {
      int count = 12 + generation * 6;

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(random.nextDouble()) * (double)radius;
         level.addParticle(
            (ParticleOptions)(random.nextInt(3) == 0 ? IronParticles.or("irons_spellbooks:firefly", GOLD) : LEAF),
            at.x + Math.cos(a) * rr,
            at.y + (double)height * (0.4 + random.nextDouble() * 0.6),
            at.z + Math.sin(a) * rr,
            (random.nextDouble() - 0.5) * 0.25,
            -0.05 - random.nextDouble() * 0.15,
            (random.nextDouble() - 0.5) * 0.25
         );
      }
   }
}
