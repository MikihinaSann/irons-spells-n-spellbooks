package com.grandsigil.client;

import com.grandsigil.entity.PhoenixEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class PhoenixParticles {
   private static final DustParticleOptions CINDER = new DustParticleOptions(new Vector3f(1.0F, 0.45F, 0.08F), 2.0F);
   private static final DustParticleOptions SPARK = new DustParticleOptions(new Vector3f(1.0F, 0.82F, 0.4F), 1.2F);

   private PhoenixParticles() {
   }

   public static void tick(PhoenixEntity phoenix) {
      Level level = phoenix.level();
      RandomSource random = level.random;
      float t = phoenix.age(0.0F);
      double ox = phoenix.getX();
      double oy = phoenix.getY();
      double oz = phoenix.getZ();
      if (t < 34.0F) {
         Vector3f s = phoenix.start();

         for (int i = 0; i < 6; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = random.nextDouble() * 3.6;
            level.addParticle(
               ParticleTypes.FLAME,
               ox + (double)s.x + Math.cos(a) * r,
               oy + (double)s.y - 1.0 + random.nextDouble(),
               oz + (double)s.z + Math.sin(a) * r,
               0.0,
               0.18 + random.nextDouble() * 0.25,
               0.0
            );
         }
      }

      if (t < 236.0F) {
         PhoenixPose pose = PhoenixPose.flight(phoenix, t);
         if (pose.alpha > 0.3F) {
            for (int i = 0; i < 4; i++) {
               int w = random.nextInt(2);
               Vector3f at = pose.along(w, random.nextFloat());
               at.add(new Vector3f(pose.fwd).mul(-random.nextFloat() * 2.5F * pose.scale));
               level.addParticle(
                  ParticleTypes.FLAME,
                  ox + (double)at.x,
                  oy + (double)at.y,
                  oz + (double)at.z,
                  (double)(-pose.fwd.x) * 0.05,
                  -0.02,
                  (double)(-pose.fwd.z) * 0.05
               );
            }

            for (int i = 0; i < 2; i++) {
               int w = random.nextInt(2);
               Vector3f at = pose.along(w, 0.5F + 0.5F * random.nextFloat());
               level.addParticle(CINDER, ox + (double)at.x, oy + (double)at.y, oz + (double)at.z, 0.0, -0.05, 0.0);
            }

            if (random.nextInt(3) == 0) {
               level.addParticle(ParticleTypes.LAVA, ox + (double)pose.at.x, oy + (double)pose.at.y, oz + (double)pose.at.z, 0.0, 0.0, 0.0);
            }

            Vector3f tail = new Vector3f(pose.at).add(new Vector3f(pose.fwd).mul(-2.4F * pose.scale));
            level.addParticle(ParticleTypes.SMALL_FLAME, ox + (double)tail.x, oy + (double)tail.y, oz + (double)tail.z, 0.0, 0.0, 0.0);
         }
      }

      if (t >= 62.0F) {
         float[] floor = phoenix.floor();
         Vector3f s = phoenix.start();
         float e = phoenix.entry();
         float turn = phoenix.turn();

         for (int n = 0; n < 24; n++) {
            int i = random.nextInt(576);
            float b = PhoenixEntity.burning(i, t);
            if (!(b < 0.3F)) {
               Vector3f p = PhoenixEntity.at(PhoenixEntity.trailTime(i), s, e, turn);
               double x = ox + (double)p.x + (random.nextDouble() - 0.5) * 5.0;
               double z = oz + (double)p.z + (random.nextDouble() - 0.5) * 5.0;
               double y = oy + (double)floor[i] + 0.1;
               level.addParticle(ParticleTypes.FLAME, x, y + random.nextDouble() * 0.8, z, 0.0, 0.05 + random.nextDouble() * 0.06, 0.0);
               if (n % 3 == 0) {
                  level.addParticle(ParticleTypes.LARGE_SMOKE, x, y + 1.6, z, 0.0, 0.05, 0.0);
               }

               if (n == 0 && random.nextInt(3) == 0) {
                  level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x, y + 1.0, z, 0.0, 0.07, 0.0);
               }

               if (n == 1 && random.nextInt(4) == 0) {
                  level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.0, 0.0);
               }
            }
         }
      }

      float since = t - 236.0F;
      if (since >= 0.0F && since < 30.0F) {
         for (int i = 0; i < 10; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = random.nextDouble() * 6.0;
            level.addParticle(
               ParticleTypes.FLAME,
               ox + Math.cos(a) * r,
               oy + 0.5 + random.nextDouble() * 14.0,
               oz + Math.sin(a) * r,
               0.0,
               0.35 + random.nextDouble() * 0.4,
               0.0
            );
         }

         if (since < 14.0F) {
            for (int i = 0; i < 6; i++) {
               level.addParticle(
                  SPARK, ox + (random.nextDouble() - 0.5) * 20.0, oy + random.nextDouble() * 16.0, oz + (random.nextDouble() - 0.5) * 20.0, 0.0, 0.3, 0.0
               );
            }
         }
      }

      float ghost = since - 10.0F;
      if (ghost > 16.0F && ghost < 44.0F) {
         float u = Mth.clamp(ghost / 44.0F, 0.0F, 1.0F);
         double rise = (double)(4.0F + 34.0F * (1.0F - (1.0F - u) * (1.0F - u)));

         for (int i = 0; i < 8; i++) {
            double side = (random.nextDouble() * 2.0 - 1.0) * 20.0;
            double ang = random.nextDouble() * Math.PI * 2.0;
            double xx = ox + Math.cos(ang) * side;
            double zx = oz + Math.sin(ang) * side;
            double yx = oy + rise + (random.nextDouble() - 0.3) * 9.0;
            level.addParticle(random.nextBoolean() ? CINDER : SPARK, xx, yx, zx, 0.0, 0.03, 0.0);
         }
      }
   }
}
