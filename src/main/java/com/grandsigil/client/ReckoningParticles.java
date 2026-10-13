package com.grandsigil.client;

import com.grandsigil.entity.ReckoningEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ReckoningParticles {
   private static final DustParticleOptions SEED = new DustParticleOptions(new Vector3f(0.78F, 1.0F, 0.84F), 0.9F);
   private static final DustParticleOptions MOTE = new DustParticleOptions(new Vector3f(1.0F, 0.93F, 0.66F), 1.5F);

   private ReckoningParticles() {
   }

   public static void ambient(ReckoningEntity star) {
      switch (star.stage()) {
         case 0:
            gathering(star);
         case 1:
         default:
            break;
         case 2:
         case 3:
            standing(star, 1.0F);
            break;
         case 4:
            standing(star, star.standing(0.0F));
      }
   }

   private static void gathering(ReckoningEntity star) {
      Level level = star.level();
      Entity caster = level.getEntity(star.casterId());
      if (caster != null) {
         RandomSource random = level.random;
         float fill = Mth.clamp((float)star.stageAge() / (float)star.gather(), 0.0F, 1.0F);
         Vec3 hands = caster.position().add(0.0, (double)caster.getBbHeight() * 0.72, 0.0);
         int count = 1 + Mth.floor(fill * 4.0F);

         for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = 0.9 + random.nextDouble() * 1.4;
            double x = hands.x + Math.cos(a) * rr;
            double y = hands.y + (random.nextDouble() - 0.5) * 1.2;
            double z = hands.z + Math.sin(a) * rr;
            Vec3 toward = new Vec3(hands.x - x, hands.y - y, hands.z - z).normalize().scale(0.1 + (double)fill * 0.22);
            level.addParticle(SEED, x, y, z, toward.x, toward.y, toward.z);
         }
      }
   }

   private static void standing(ReckoningEntity star, float strength) {
      if (!(strength <= 0.02F)) {
         Level level = star.level();
         RandomSource random = level.random;
         Vec3 at = star.position();
         float radius = star.radius();
         float tall = 1.0F - star.closing(0.0F);
         int motes = scatter(random, (3.0F + radius / 2.5F) * strength);

         for (int i = 0; i < motes; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(random.nextDouble()) * (double)radius;
            level.addParticle(
               MOTE,
               at.x + Math.cos(a) * rr,
               at.y + random.nextDouble() * 14.0 * (double)tall,
               at.z + Math.sin(a) * rr,
               0.0,
               0.24 + random.nextDouble() * 0.35,
               0.0
            );
         }

         if (random.nextFloat() < 0.5F * strength) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = random.nextDouble() * (double)radius * 0.35;
            level.addParticle(ParticleTypes.END_ROD, at.x + Math.cos(a) * rr, at.y + 0.4, at.z + Math.sin(a) * rr, 0.0, 0.45 + random.nextDouble() * 0.4, 0.0);
         }

         if (random.nextFloat() < 0.34F * strength) {
            double a = random.nextDouble() * Math.PI * 2.0;
            level.addParticle(ParticleTypes.END_ROD, at.x + Math.cos(a) * (double)radius, at.y + 0.2, at.z + Math.sin(a) * (double)radius, 0.0, 0.18, 0.0);
         }
      }
   }

   private static int scatter(RandomSource random, float n) {
      int whole = Mth.floor(n);
      return random.nextFloat() < n - (float)whole ? whole + 1 : whole;
   }
}
