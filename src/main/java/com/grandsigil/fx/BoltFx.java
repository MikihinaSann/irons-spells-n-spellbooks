package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class BoltFx {
   private BoltFx() {
   }

   public static void gathering(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 6.0F, 0.3F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 4.0F, 0.35F);
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 2.5F, 0.4F);
   }

   public static void charging(ServerLevel level, Vec3 at, double radius, float progress) {
      GsFx.play(level, at, "minecraft:block.conduit.ambient", SoundEvents.CONDUIT_AMBIENT, 1.6F, 0.5F + progress * 1.1F);
      if (level.random.nextFloat() < 0.25F + progress * 0.5F) {
         GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 2.2F, 0.28F + progress * 0.5F);
      }

      int count = 3 + Mth.floor(progress * 14.0F);

      for (int i = 0; i < count; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius;
         level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x + Math.cos(a) * rr, at.y + 0.2, at.z + Math.sin(a) * rr, 1, 0.2, 0.3, 0.2, 0.08);
      }
   }

   public static void struck(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 9.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 7.0F, 0.8F);
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 6.0F, 0.4F);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 3.0, at.z, 10, 2.0, 3.0, 2.0, 0.0);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y + 1.0, at.z, 260, 5.0, 3.0, 5.0, 1.2);
      level.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y + 2.0, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      int points = 140;

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x + Math.cos(a) * radius, at.y + 0.4, at.z + Math.sin(a) * radius, 1, 0.2, 0.4, 0.2, 0.12);
      }

      CameraShakeManager.addCameraShake(new CameraShakeData(level, 50, at, (float)(radius * 2.5)));
   }

   public static void ionised(ServerLevel level, Vec3 at, double radius, int tick) {
      if (tick == 25) {
         GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 10.0F, 0.35F);
      }

      if (tick <= 90) {
         float fade = 1.0F - (float)tick / 90.0F;
         if (level.random.nextFloat() < fade * 0.5F) {
            double a = level.random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(level.random.nextDouble()) * radius;
            Vec3 spark = at.add(Math.cos(a) * rr, 0.5, Math.sin(a) * rr);
            GsFx.play(level, spark, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 0.5F, 1.4F + level.random.nextFloat() * 0.5F);
         }

         int count = Mth.floor(fade * 12.0F);

         for (int i = 0; i < count; i++) {
            double a = level.random.nextDouble() * Math.PI * 2.0;
            double rr = Math.sqrt(level.random.nextDouble()) * radius;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x + Math.cos(a) * rr, at.y + 0.3, at.z + Math.sin(a) * rr, 2, 0.3, 0.5, 0.3, 0.15);
         }
      }
   }
}
