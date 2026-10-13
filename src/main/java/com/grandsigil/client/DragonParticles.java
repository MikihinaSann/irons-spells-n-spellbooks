package com.grandsigil.client;

import com.grandsigil.entity.ThunderDragonEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class DragonParticles {
   private DragonParticles() {
   }

   public static void tick(ThunderDragonEntity dragon) {
      Level level = dragon.level();
      RandomSource random = level.random;
      float storm = dragon.storm(0.0F);
      double ox = dragon.getX();
      double oy = dragon.getY();
      double oz = dragon.getZ();
      if (storm > 0.1F) {
         int drops = Mth.floor(30.0F * storm);

         for (int i = 0; i < drops; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = Math.sqrt(random.nextDouble()) * 64.0 * 0.8 * (double)storm;
            level.addParticle(
               ParticleTypes.FALLING_WATER, ox + Math.cos(a) * r, oy + 48.0 - 6.0 - random.nextDouble() * 10.0, oz + Math.sin(a) * r, 0.0, -0.6, 0.0
            );
         }
      }

      float flight = dragon.flight(0.0F);
      if (flight > 0.0F && flight < (float)(275 + ThunderDragonEntity.POUR)) {
         for (int i = 0; i < 14; i++) {
            float ts = flight - random.nextFloat() * 150.0F / 3.2F;
            if (!(ts <= 0.0F) && !(ts > 275.0F)) {
               Vector3f p = ThunderDragonEntity.at(ts, dragon.entry(), dragon.turn(), dragon.finalPoint());
               level.addParticle(
                  ParticleTypes.ELECTRIC_SPARK,
                  ox + (double)p.x + (random.nextDouble() - 0.5) * 9.0,
                  oy + (double)p.y + (random.nextDouble() - 0.5) * 9.0,
                  oz + (double)p.z + (random.nextDouble() - 0.5) * 9.0,
                  (random.nextDouble() - 0.5) * 0.5,
                  (random.nextDouble() - 0.5) * 0.5,
                  (random.nextDouble() - 0.5) * 0.5
               );
            }
         }
      }

      float since = flight - 275.0F;
      if (since >= 0.0F && since < (float)ThunderDragonEntity.POUR) {
         Vector3f f = dragon.finalPoint();

         for (int ix = 0; ix < 16; ix++) {
            level.addParticle(
               ParticleTypes.ELECTRIC_SPARK,
               ox + (double)f.x + (random.nextDouble() - 0.5) * 6.0,
               oy + (double)f.y + random.nextDouble() * 3.0,
               oz + (double)f.z + (random.nextDouble() - 0.5) * 6.0,
               (random.nextDouble() - 0.5) * 1.2,
               random.nextDouble() * 1.2,
               (random.nextDouble() - 0.5) * 1.2
            );
         }
      }
   }
}
