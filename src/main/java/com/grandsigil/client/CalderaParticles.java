package com.grandsigil.client;

import com.grandsigil.entity.CalderaEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class CalderaParticles {
   private static final DustParticleOptions EMBER = new DustParticleOptions(new Vector3f(1.0F, 0.42F, 0.1F), 1.4F);

   private CalderaParticles() {
   }

   public static void ambient(CalderaEntity field) {
      Level level = field.level();
      RandomSource random = level.random;
      Vec3 at = field.position();
      float radius = field.radius();
      float spread = field.spread(0.0F);
      if (!(spread <= 0.02F)) {
         int embers = 2 + Mth.floor(radius * spread / 6.0F);

         for (int i = 0; i < embers; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius * (double)spread;
            level.addParticle(
               EMBER, at.x + Math.cos(a) * rr, at.y + 0.2 + random.nextDouble() * 0.6, at.z + Math.sin(a) * rr, 0.0, 0.05 + random.nextDouble() * 0.08, 0.0
            );
         }

         if (random.nextInt(2) == 0) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius * (double)spread;
            level.addParticle(ParticleTypes.SMOKE, at.x + Math.cos(a) * rr, at.y + 0.3, at.z + Math.sin(a) * rr, 0.0, 0.03, 0.0);
         }

         if (field.stage() != 2 && random.nextInt(2) == 0) {
            double a = random.nextDouble() * Math.PI * 2.0;
            level.addParticle(
               ParticleTypes.FLAME,
               at.x + Math.cos(a) * (double)radius * (double)spread,
               at.y + 0.3,
               at.z + Math.sin(a) * (double)radius * (double)spread,
               0.0,
               0.02,
               0.0
            );
         }
      }
   }
}
