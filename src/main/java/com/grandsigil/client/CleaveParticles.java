package com.grandsigil.client;

import com.grandsigil.entity.CleaveEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class CleaveParticles {
   private CleaveParticles() {
   }

   public static void tick(CleaveEntity cleave) {
      Level level = cleave.level();
      RandomSource random = level.random;
      float t = cleave.age(0.0F);
      float heading = cleave.heading();
      Vec3 centre = cleave.position();
      if (t < 20.0F) {
         for (int i = 0; i < 6; i++) {
            float s = random.nextFloat() * 2.0F - 1.0F;
            Vec3 p = centre.add(CleaveEntity.along(heading, s));
            level.addParticle(
               ParticleTypes.REVERSE_PORTAL,
               p.x + (random.nextDouble() - 0.5) * 6.0,
               p.y + (random.nextDouble() - 0.5) * 6.0,
               p.z + (random.nextDouble() - 0.5) * 6.0,
               0.0,
               0.0,
               0.0
            );
         }
      } else if (t < 83.0F) {
         float open = CleaveEntity.open(t);

         for (int i = 0; i < (int)(14.0F * open); i++) {
            float s = random.nextFloat() * 2.0F - 1.0F;
            Vec3 p = centre.add(CleaveEntity.along(heading, s));
            double w = (double)(16.0F * CleaveEntity.profile(s) * open);
            double ox = (random.nextDouble() - 0.5) * w * 2.0;
            double oy = (random.nextDouble() - 0.5) * w * 1.5;
            double oz = (random.nextDouble() - 0.5) * w * 2.0;
            level.addParticle(ParticleTypes.PORTAL, p.x, p.y, p.z, ox, oy, oz);
         }
      } else if (t < 95.0F) {
         for (int i = 0; i < 20; i++) {
            float s = random.nextFloat() * 2.0F - 1.0F;
            Vec3 p = centre.add(CleaveEntity.along(heading, s));
            level.addParticle(
               ParticleTypes.END_ROD, p.x, p.y, p.z, (random.nextDouble() - 0.5) * 0.8, (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.8
            );
         }
      }
   }
}
