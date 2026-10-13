package com.grandsigil.client;

import com.grandsigil.entity.IceAgeEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public final class IceAgeParticles {
   private IceAgeParticles() {
   }

   public static void tick(IceAgeEntity storm) {
      Level level = storm.level();
      RandomSource random = level.random;
      float t = storm.age(0.0F);
      double ox = storm.getX();
      double oy = storm.getY();
      double oz = storm.getZ();
      float reach = 56.0F * Math.max(0.2F, IceAgeEntity.frost(t));
      if (t < 244.0F) {
         float wind = IceAgeEntity.vortex(t) * (1.0F - IceAgeEntity.zero(t));

         for (int i = 0; i < (int)(16.0F * wind); i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = Math.sqrt(random.nextDouble()) * (double)reach;
            double x = ox + Math.cos(a) * r;
            double z = oz + Math.sin(a) * r;
            double s = 0.9 / Math.max(4.0, r * 0.2);
            level.addParticle(ParticleTypes.SNOWFLAKE, x, oy + 0.5 + random.nextDouble() * 12.0, z, -Math.sin(a) * s, 0.02, Math.cos(a) * s);
         }

         for (int i = 0; i < 3; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = Math.sqrt(random.nextDouble()) * (double)reach;
            level.addParticle(ParticleTypes.WHITE_ASH, ox + Math.cos(a) * r, oy + 0.3, oz + Math.sin(a) * r, 0.0, 0.02, 0.0);
         }
      } else if (t < 300.0F) {
         for (int i = 0; i < 14; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = Math.sqrt(random.nextDouble()) * 56.0;
            level.addParticle(ParticleTypes.SNOWFLAKE, ox + Math.cos(a) * r, oy + 8.0 + random.nextDouble() * 20.0, oz + Math.sin(a) * r, 0.0, -0.05, 0.0);
         }
      }

      for (int i = 0; i < 28; i++) {
         int land = IceAgeEntity.shardStart(i) + 10;
         if ((int)t == land) {
            Vec3 off = IceAgeEntity.shardTarget(storm.seed(), i);
            double x = ox + off.x;
            double z = oz + off.z;
            double y = (double)level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)Math.floor(x), (int)Math.floor(z));

            for (int k = 0; k < 30; k++) {
               double a = random.nextDouble() * Math.PI * 2.0;
               double sp = 0.2 + random.nextDouble() * 0.5;
               level.addParticle(ParticleTypes.SNOWFLAKE, x, y + 0.3, z, Math.cos(a) * sp, 0.15 + random.nextDouble() * 0.3, Math.sin(a) * sp);
            }
         }
      }
   }
}
