package com.grandsigil.client;

import com.grandsigil.entity.CoffinEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class CoffinParticles {
   private static final DustParticleOptions SEAM = new DustParticleOptions(new Vector3f(0.72F, 0.26F, 0.98F), 1.4F);
   private static final DustParticleOptions CORE = new DustParticleOptions(new Vector3f(0.98F, 0.36F, 1.0F), 1.1F);

   private CoffinParticles() {
   }

   public static void ambient(CoffinEntity coffin) {
      Level level = coffin.level();
      RandomSource random = level.random;
      Vec3 at = coffin.position();
      float half = coffin.half();
      float tall = coffin.height();
      float up = coffin.sealed(0.0F);
      float gone = coffin.breaking(0.0F);
      if (!(up <= 0.02F)) {
         float left = 1.0F - gone;
         int seams = Mth.floor((2.0F + half * 0.7F) * left);

         for (int i = 0; i < seams; i++) {
            Vec3 edge = onWall(random, at, half, 1.02);
            double y = at.y + random.nextDouble() * (double)tall * (double)up;
            level.addParticle(
               SEAM, edge.x, y, edge.z, (random.nextDouble() - 0.5) * 0.02, 0.02 + random.nextDouble() * 0.05, (random.nextDouble() - 0.5) * 0.02
            );
         }

         if (coffin.stage() == 1 && random.nextInt(2) == 0) {
            Vec3 edge = onWall(random, at, half, 1.0);
            level.addParticle(
               CORE, edge.x, at.y + (double)tall - 0.15, edge.z, (edge.x - at.x) * 0.004, 0.06 + random.nextDouble() * 0.06, (edge.z - at.z) * 0.004
            );
         }

         if (coffin.stage() == 0) {
            for (int i = 0; i < 3; i++) {
               double x = at.x + (random.nextDouble() * 2.0 - 1.0) * (double)half * 0.8;
               double z = at.z + (random.nextDouble() * 2.0 - 1.0) * (double)half * 0.8;
               level.addParticle(CORE, x, at.y + (double)(tall * up) * 0.9, z, 0.0, 0.1 + random.nextDouble() * 0.16, 0.0);
            }
         }

         if (gone > 0.0F) {
            int dust = Mth.floor((4.0F + half) * left);

            for (int i = 0; i < dust; i++) {
               Vec3 edge = onWall(random, at, half, 0.95 + random.nextDouble() * 0.25);
               level.addParticle(
                  ParticleTypes.LARGE_SMOKE,
                  edge.x,
                  at.y + random.nextDouble() * (double)tall,
                  edge.z,
                  (random.nextDouble() - 0.5) * 0.05,
                  0.01 + random.nextDouble() * 0.04,
                  (random.nextDouble() - 0.5) * 0.05
               );
            }
         }
      }
   }

   private static Vec3 onWall(RandomSource random, Vec3 at, float half, double out) {
      double along = (random.nextDouble() * 2.0 - 1.0) * (double)half;
      double edge = (double)half * out;

      return switch (random.nextInt(4)) {
         case 0 -> new Vec3(at.x + along, at.y, at.z - edge);
         case 1 -> new Vec3(at.x + along, at.y, at.z + edge);
         case 2 -> new Vec3(at.x - edge, at.y, at.z + along);
         default -> new Vec3(at.x + edge, at.y, at.z + along);
      };
   }
}
