package com.grandsigil.client;

import com.grandsigil.entity.BloomEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class BloomParticles {
   private static final DustParticleOptions POLLEN = new DustParticleOptions(new Vector3f(1.0F, 0.9F, 0.45F), 1.4F);

   private BloomParticles() {
   }

   public static void tick(BloomEntity bloom) {
      Level level = bloom.level();
      RandomSource random = level.random;
      float t = bloom.age(0.0F);
      float field = BloomEntity.field(t);
      double ox = bloom.getX();
      double oy = bloom.getY();
      double oz = bloom.getZ();
      double top = oy + (double)(46.0F * BloomEntity.growth(t));

      for (int i = 0; i < (int)(10.0F * field); i++) {
         double a = random.nextDouble() * Math.PI * 2.0;
         double r = Math.sqrt(random.nextDouble()) * 48.0;
         level.addParticle(ParticleTypes.CHERRY_LEAVES, ox + Math.cos(a) * r, top - random.nextDouble() * 10.0, oz + Math.sin(a) * r, 0.0, 0.0, 0.0);
         level.addParticle(
            ParticleTypes.SPORE_BLOSSOM_AIR, ox + Math.cos(a + 1.0) * r, oy + random.nextDouble() * 3.0, oz + Math.sin(a + 1.0) * r, 0.0, 0.0, 0.0
         );
         if (i % 2 == 0) {
            level.addParticle(POLLEN, ox + Math.cos(a + 2.0) * r, oy + 1.0 + random.nextDouble() * 6.0, oz + Math.sin(a + 2.0) * r, 0.0, 0.03, 0.0);
         }
      }

      if (BloomEntity.growth(t) > 0.05F && t < 270.0F) {
         for (int ix = 0; ix < 4; ix++) {
            double h = random.nextDouble() * (top - oy);
            double a = random.nextDouble() * Math.PI * 2.0;
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, ox + Math.cos(a) * 3.0, oy + h, oz + Math.sin(a) * 3.0, 0.0, 0.05, 0.0);
         }
      }

      if (t >= 270.0F && t < 320.0F) {
         for (int ix = 0; ix < 18; ix++) {
            level.addParticle(
               ParticleTypes.CHERRY_LEAVES,
               ox + (random.nextDouble() - 0.5) * 30.0,
               top + (random.nextDouble() - 0.5) * 10.0,
               oz + (random.nextDouble() - 0.5) * 30.0,
               0.0,
               0.0,
               0.0
            );
            level.addParticle(
               ParticleTypes.END_ROD,
               ox + (random.nextDouble() - 0.5) * 8.0,
               oy + random.nextDouble() * (top - oy),
               oz + (random.nextDouble() - 0.5) * 8.0,
               0.0,
               0.08,
               0.0
            );
         }
      }
   }
}
