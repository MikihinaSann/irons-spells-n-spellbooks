package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class GlacierFx {
   private GlacierFx() {
   }

   public static void looming(ServerLevel level, Vec3 at, double radius, float progress) {
      if (progress < 0.02F) {
         GsFx.play(level, at, "irons_spellbooks:frostwave_prepare", SoundEvents.BEACON_ACTIVATE, 5.0F, 0.3F);
         GsFx.play(level, at, "minecraft:entity.wither.spawn", SoundEvents.WITHER_SPAWN, 3.0F, 0.35F);
      }

      double sky = at.y + 159.79999999999998;
      int fall = (int)Math.max(24.0, radius * 1.4);

      for (int i = 0; i < fall; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius * 1.05;
         level.sendParticles(
            IronParticles.or("irons_spellbooks:snow_dust", ParticleTypes.SNOWFLAKE),
            at.x + Math.cos(a) * rr,
            sky - level.random.nextDouble() * 8.0,
            at.z + Math.sin(a) * rr,
            1,
            0.6,
            0.4,
            0.6,
            0.02
         );
      }

      if (progress > 0.45F) {
         GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 2.0F, 0.35F + 0.2F * progress);
      }
   }

   public static void falling(ServerLevel level, Vec3 at, double radius, float near) {
      GsFx.play(level, at, "minecraft:entity.wither.shoot", SoundEvents.WITHER_SHOOT, 2.0F + 4.0F * near, 0.28F);
   }

   public static void lands(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 6.0F, 0.28F);
      GsFx.play(level, at, "minecraft:block.deepslate.break", SoundEvents.DEEPSLATE_BREAK, 6.0F, 0.3F);
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 5.0F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 5.0F, 0.5F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 10, at, (float)radius * 3.0F));
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 26, at, (float)radius * 4.2F));
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 60, at, (float)radius * 2.6F));
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 100, at, (float)radius * 1.8F));
      IronParticles.flatRing(level, at.x, at.y + 0.2, at.z);
      int points = (int)Math.max(120.0, radius * 7.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points + level.random.nextDouble() * 0.05;
         double rr = radius * (0.94 + level.random.nextDouble() * 0.5);
         level.sendParticles(
            IronParticles.or("irons_spellbooks:snow_dust", ParticleTypes.SNOWFLAKE),
            at.x + Math.cos(a) * rr,
            at.y + 0.6,
            at.z + Math.sin(a) * rr,
            3,
            0.4,
            0.5,
            0.4,
            0.42
         );
         if (i % 3 == 0) {
            level.sendParticles(ParticleTypes.EXPLOSION, at.x + Math.cos(a) * rr, at.y + 1.6, at.z + Math.sin(a) * rr, 1, 0.6, 0.4, 0.6, 0.0);
         }
      }

      for (int ix = 0; ix < 70; ix++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         level.sendParticles(ParticleTypes.ITEM_SNOWBALL, at.x + Math.cos(a) * radius, at.y + 1.0, at.z + Math.sin(a) * radius, 2, 0.5, 0.5, 0.5, 0.9);
      }
   }

   public static void rising(ServerLevel level, Vec3 at, double radius, float progress) {
      GsFx.play(level, at, "minecraft:block.deepslate.place", SoundEvents.DEEPSLATE_PLACE, 2.4F, 0.35F + progress * 0.4F);
      GsFx.play(level, at, "minecraft:block.glass.place", SoundEvents.GLASS_PLACE, 1.6F, 0.5F + progress * 0.5F);
      int count = 20 + Math.round(progress * 40.0F);

      for (int i = 0; i < count; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius;
         level.sendParticles(
            IronParticles.or("irons_spellbooks:snow_dust", ParticleTypes.SNOWFLAKE),
            at.x + Math.cos(a) * rr,
            at.y + 0.3,
            at.z + Math.sin(a) * rr,
            1,
            0.3,
            0.5,
            0.3,
            0.05
         );
      }
   }

   public static void groaning(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.deepslate.step", SoundEvents.DEEPSLATE_STEP, 2.2F, 0.3F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 1.4F, 0.4F);
   }

   public static void sealed(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      GsFx.play(level, at, "minecraft:block.glass.place", SoundEvents.GLASS_PLACE, 1.8F, 0.6F);
      level.sendParticles(IronParticles.or("irons_spellbooks:snowflake", ParticleTypes.SNOWFLAKE), at.x, at.y, at.z, 40, 0.5, 0.9, 0.5, 0.08);
      level.sendParticles(ParticleTypes.ITEM_SNOWBALL, at.x, at.y, at.z, 20, 0.4, 0.8, 0.4, 0.1);
   }

   public static void shatters(ServerLevel level, Vec3 at, double radius, int sealed) {
      GsFx.play(level, at, "minecraft:block.deepslate.break", SoundEvents.DEEPSLATE_BREAK, 5.0F, 0.28F);
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 5.0F, 0.7F);
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 4.0F, 1.1F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 3.5F, 0.5F);
      GsFx.play(level, at, "minecraft:block.powder_snow.break", SoundEvents.POWDER_SNOW_BREAK, 4.0F, 0.5F);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 6.0, at.z, 2, radius * 0.4, 3.0, radius * 0.4, 0.0);
      level.sendParticles(ParticleTypes.ITEM_SNOWBALL, at.x, at.y + 4.0, at.z, 220, radius * 0.5, 6.0, radius * 0.5, 0.9);
      level.sendParticles(
         IronParticles.or("irons_spellbooks:snow_dust", ParticleTypes.SNOWFLAKE), at.x, at.y + 3.0, at.z, 160, radius * 0.5, 5.0, radius * 0.5, 0.7
      );
      int spokes = (int)Math.max(48.0, radius * 3.0);

      for (int i = 0; i < spokes; i++) {
         double a = (Math.PI * 2) * (double)i / (double)spokes;
         level.sendParticles(
            ParticleTypes.ITEM_SNOWBALL, at.x + Math.cos(a) * 2.0, at.y + 1.0, at.z + Math.sin(a) * 2.0, 0, Math.cos(a), 0.12, Math.sin(a), 1.6
         );
      }

      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
   }
}
