package com.grandsigil.client;

import com.grandsigil.entity.ArenaEntity;
import com.grandsigil.fx.IronParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ArenaParticles {
   private static final DustParticleOptions SPRAY = new DustParticleOptions(new Vector3f(0.6F, 0.05F, 0.08F), 1.0F);
   private static final DustParticleOptions DUST = new DustParticleOptions(new Vector3f(0.44F, 0.06F, 0.08F), 1.3F);

   private ArenaParticles() {
   }

   public static void ambient(ArenaEntity arena) {
      Level level = arena.level();
      RandomSource random = level.random;
      Vec3 at = arena.position();
      float radius = arena.radius();
      float tall = arena.height() * arena.built(0.0F);
      switch (arena.stage()) {
         case 0:
            rising(level, random, at, radius, tall, arena.built(0.0F));
            break;
         case 2:
            falling(level, random, at, radius, tall, arena.falling(0.0F));
            break;
         default:
            holding(level, random, at, radius, tall);
      }
   }

   private static void rising(Level level, RandomSource random, Vec3 at, float radius, float tall, float built) {
      for (int i = 0; i < 26; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         level.addParticle(
            (ParticleOptions)(random.nextInt(3) == 0 ? ParticleTypes.POOF : DUST),
            at.x + Math.cos(a) * (double)radius * (0.94 + random.nextDouble() * 0.1),
            at.y + random.nextDouble() * (double)tall,
            at.z + Math.sin(a) * (double)radius * (0.94 + random.nextDouble() * 0.1),
            0.0,
            0.1 + random.nextDouble() * 0.2,
            0.0
         );
      }
   }

   private static void holding(Level level, RandomSource random, Vec3 at, float radius, float tall) {
      float inner = radius - 3.5F;

      for (int i = 0; i < 22; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double h = Math.pow(random.nextDouble(), 0.7) * (double)tall;
         level.addParticle(
            IronParticles.or("irons_spellbooks:blood", SPRAY),
            at.x + Math.cos(a) * (double)inner,
            at.y + h,
            at.z + Math.sin(a) * (double)inner,
            (random.nextDouble() - 0.5) * 0.06,
            -0.25 - random.nextDouble() * 0.3,
            (random.nextDouble() - 0.5) * 0.06
         );
      }
   }

   private static void falling(Level level, RandomSource random, Vec3 at, float radius, float tall, float fall) {
      int count = Math.round((1.0F - fall) * 44.0F);

      for (int i = 0; i < count; i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double rr = (double)radius * (0.7 + random.nextDouble() * 0.35);
         level.addParticle(
            (ParticleOptions)(random.nextInt(3) == 0 ? DUST : ParticleTypes.POOF),
            at.x + Math.cos(a) * rr,
            at.y + random.nextDouble() * (double)tall * (1.0 - (double)fall),
            at.z + Math.sin(a) * rr,
            -Math.cos(a) * 0.2,
            -0.4 - random.nextDouble() * 0.4,
            -Math.sin(a) * 0.2
         );
      }
   }
}
