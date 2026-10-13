package com.grandsigil.client;

import com.grandsigil.entity.ArsenalEntity;
import com.grandsigil.entity.BladeEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ArsenalParticles {
   private static final DustParticleOptions GOLD = new DustParticleOptions(new Vector3f(1.0F, 0.8F, 0.32F), 0.9F);
   private static final DustParticleOptions PALE = new DustParticleOptions(new Vector3f(1.0F, 0.95F, 0.78F), 1.2F);

   private ArsenalParticles() {
   }

   public static void ambient(ArsenalEntity armoury) {
      float open = armoury.open(0.0F);
      if (!(open <= 0.05F)) {
         Level level = armoury.level();
         RandomSource random = level.random;
         int count = 4 + Mth.floor(open * 6.0F);

         for (int n = 0; n < count; n++) {
            int i = random.nextInt(64);
            Vec3 at = ArsenalEntity.gate(armoury.position(), armoury.yaw(), i);
            float r = ArsenalEntity.size(i) * 1.6F;
            level.addParticle(
               ParticleTypes.ENCHANT,
               at.x,
               at.y,
               at.z,
               (random.nextDouble() - 0.5) * (double)r * 2.0,
               (random.nextDouble() - 0.5) * (double)r * 2.0,
               (random.nextDouble() - 0.5) * (double)r * 2.0
            );
         }
      }
   }

   public static void blade(BladeEntity blade) {
      Level level = blade.level();
      RandomSource random = level.random;
      Vector3f d = blade.dir();
      float length = blade.reach();
      if (blade.state() == 1) {
         Vec3 tail = blade.position().subtract((double)(d.x * length), (double)(d.y * length), (double)(d.z * length));
         level.addParticle(GOLD, tail.x, tail.y, tail.z, 0.0, 0.0, 0.0);
         if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.END_ROD, tail.x, tail.y, tail.z, (double)(-d.x) * 0.02, (double)(-d.y) * 0.02, (double)(-d.z) * 0.02);
         }
      } else if (blade.state() == 3) {
         for (int n = 0; n < 2; n++) {
            double t = random.nextDouble() * (double)length;
            Vec3 at = blade.position().subtract((double)d.x * t, (double)d.y * t, (double)d.z * t);
            level.addParticle(PALE, at.x, at.y, at.z, 0.0, 0.04 + random.nextDouble() * 0.04, 0.0);
         }
      }
   }
}
