package com.grandsigil.client;

import com.grandsigil.entity.SigilEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class SigilParticles {
   private static final Vector3f GOLD = new Vector3f(1.0F, 0.78F, 0.3F);
   private static final Vector3f PALE = new Vector3f(1.0F, 0.95F, 0.78F);
   private static final Vector3f VIOLET = new Vector3f(0.62F, 0.42F, 1.0F);
   private static final DustParticleOptions DUST_GOLD = new DustParticleOptions(GOLD, 0.9F);
   private static final DustParticleOptions DUST_PALE = new DustParticleOptions(PALE, 0.7F);
   private static final DustParticleOptions DUST_VIOLET = new DustParticleOptions(VIOLET, 0.8F);
   private static final float RISE = 0.36F;
   private static final float BASE = 0.55F;

   private SigilParticles() {
   }

   public static void ambient(SigilEntity sigil) {
      Level level = sigil.level();
      RandomSource random = level.random;
      Vec3 at = sigil.centre();
      float radius = sigil.radius();
      switch (sigil.stage()) {
         case 0:
            charging(level, random, at, radius, sigil);
            break;
         case 1:
            igniting(level, random, at, radius, sigil);
            break;
         default:
            firing(level, random, at, radius, sigil);
      }
   }

   private static void charging(Level level, RandomSource random, Vec3 at, float radius, SigilEntity sigil) {
      if (sigil.stageAge() < 8) {
         float drawn = (float)sigil.stageAge() / 8.0F;
         float ang = Mth.lerp(drawn, 0.0F, (float) (Math.PI * 2)) + sigil.spin;

         for (int i = 0; i < 4; i++) {
            double rr = (double)radius * (0.94 + random.nextDouble() * 0.08);
            level.addParticle(DUST_PALE, at.x + (double)Mth.cos(ang) * rr, at.y + 0.1, at.z + (double)Mth.sin(ang) * rr, 0.0, 0.05, 0.0);
         }
      } else {
         int lit = sigil.runes();
         int embers = 2 + Mth.floor(radius / 4.0F);

         for (int i = 0; i < embers; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = (double)radius * (0.9 + random.nextDouble() * 0.1);
            level.addParticle(DUST_GOLD, at.x + Math.cos(a) * rr, at.y + 0.05, at.z + Math.sin(a) * rr, 0.0, 0.06 + 0.02 * (double)lit, 0.0);
         }

         for (int n = 0; n < lit; n++) {
            if (random.nextInt(5) == 0) {
               double ringY = (double)(radius * 0.55F + radius * 0.36F * (float)n);
               double ringR = (double)radius * (0.88 - 0.03 * (double)n) * (1.0 + 0.16 * (double)Mth.sin((float)n * 2.1F));
               double a = random.nextDouble() * Math.PI * 2.0;
               level.addParticle(DUST_PALE, at.x + Math.cos(a) * ringR, at.y + ringY, at.z + Math.sin(a) * ringR, 0.0, 0.03, 0.0);
            }
         }

         if (lit < 12 && random.nextInt(4) == 0) {
            int i = lit + random.nextInt(12 - lit);
            float ang = sigil.spin + (float)((Math.PI * 2) * (double)i / 12.0);
            double rr = (double)radius * 0.72;
            level.addParticle(DUST_VIOLET, at.x + (double)Mth.cos(ang) * rr, at.y + 0.08, at.z + (double)Mth.sin(ang) * rr, 0.0, 0.01, 0.0);
         }
      }
   }

   private static void igniting(Level level, RandomSource random, Vec3 at, float radius, SigilEntity sigil) {
      float progress = (float)sigil.stageAge() / 40.0F;
      float pull = progress * progress * progress;
      double focusY = (double)radius * 0.12 + 1.2;
      int count = 5 + Mth.floor(progress * 12.0F) + Mth.floor(radius / 3.0F);

      for (int i = 0; i < count; i++) {
         int n = random.nextInt(12);
         double ringY = (double)Mth.lerp(pull, radius * 0.55F + radius * 0.36F * (float)n, (float)focusY);
         double ringR = (double)Mth.lerp(pull, radius * 0.8F, radius * 0.05F);
         double a = random.nextDouble() * Math.PI * 2.0;
         double px = at.x + Math.cos(a) * ringR;
         double pz = at.z + Math.sin(a) * ringR;
         double speed = 0.15 + (double)progress * 0.5;
         level.addParticle(
            random.nextBoolean() ? DUST_GOLD : DUST_PALE,
            px,
            at.y + ringY,
            pz,
            (at.x - px) * speed * 0.2,
            (focusY - ringY) * speed * 0.12,
            (at.z - pz) * speed * 0.2
         );
      }

      if (random.nextInt(2) == 0) {
         level.addParticle(
            ParticleTypes.ELECTRIC_SPARK,
            at.x + (random.nextDouble() - 0.5) * 1.2,
            at.y + focusY + (random.nextDouble() - 0.5) * 1.2,
            at.z + (random.nextDouble() - 0.5) * 1.2,
            0.0,
            0.02,
            0.0
         );
      }
   }

   private static void firing(Level level, RandomSource random, Vec3 at, float radius, SigilEntity sigil) {
      int age = sigil.stageAge();
      if (age <= 24) {
         boolean perfect = sigil.perfect();
         double focusY = (double)radius * 0.12 + 1.2;
         int count = age < 4 ? 18 + Mth.floor(radius / 2.0F) : 6;

         for (int i = 0; i < count; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double lift = random.nextDouble() * 2.0 - 1.0;
            double flat = Math.sqrt(Math.max(0.0, 1.0 - lift * lift));
            double speed = 0.5 + random.nextDouble() * 1.4;
            level.addParticle(
               perfect ? DUST_PALE : DUST_GOLD, at.x, at.y + focusY, at.z, Math.cos(a) * flat * speed, lift * speed * 0.7, Math.sin(a) * flat * speed
            );
         }

         for (int i = 0; i < 3 + Mth.floor(radius / 4.0F); i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double rr = (double)radius * (0.3 + random.nextDouble() * 0.5) * (1.0 + (double)age * 0.1);
            level.addParticle(ParticleTypes.END_ROD, at.x + Math.cos(a) * rr, at.y + 0.1, at.z + Math.sin(a) * rr, Math.cos(a) * 0.2, 0.06, Math.sin(a) * 0.2);
         }
      }
   }
}
