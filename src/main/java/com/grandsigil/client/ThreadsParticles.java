package com.grandsigil.client;

import com.grandsigil.entity.ThreadsEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class ThreadsParticles {
   private static final DustParticleOptions DRIP = new DustParticleOptions(new Vector3f(0.55F, 0.01F, 0.04F), 1.6F);
   private static final DustParticleOptions MIST = new DustParticleOptions(new Vector3f(0.85F, 0.08F, 0.08F), 1.0F);

   private ThreadsParticles() {
   }

   public static void tick(ThreadsEntity threads) {
      Level level = threads.level();
      RandomSource random = level.random;
      float t = threads.age(0.0F);
      if (!(t >= 206.0F)) {
         float r = ThreadsEntity.heart(t);
         double hx = threads.getX();
         double hy = threads.getY() + 34.0 - (double)r * 0.9;
         double hz = threads.getZ();

         for (int i = 0; i < 3; i++) {
            level.addParticle(DRIP, hx + (random.nextDouble() - 0.5) * (double)r, hy, hz + (random.nextDouble() - 0.5) * (double)r, 0.0, -0.3, 0.0);
         }

         if (!(t < 46.0F)) {
            for (int id : threads.boundIds()) {
               Entity e = level.getEntity(id);
               if (e != null && random.nextInt(3) == 0) {
                  level.addParticle(
                     MIST,
                     e.getX() + (random.nextDouble() - 0.5) * 0.6,
                     e.getY() + (double)e.getBbHeight() * 0.6,
                     e.getZ() + (random.nextDouble() - 0.5) * 0.6,
                     0.0,
                     0.05,
                     0.0
                  );
               }
            }

            Entity caster = level.getEntity(threads.casterEntityId());
            if (caster != null && (int)t % 20 == 2) {
               for (int i = 0; i < 16; i++) {
                  double a = (double)i * Math.PI * 2.0 / 16.0;
                  level.addParticle(MIST, caster.getX() + Math.cos(a) * 0.9, caster.getY() + 0.2, caster.getZ() + Math.sin(a) * 0.9, 0.0, 0.12, 0.0);
               }

               level.addParticle(ParticleTypes.HEART, caster.getX(), caster.getY() + (double)caster.getBbHeight() + 0.4, caster.getZ(), 0.0, 0.1, 0.0);
            }
         }
      }
   }
}
