package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class DragonFx {
   private DragonFx() {
   }

   public static void gathers(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:weather.rain.above", SoundEvents.WEATHER_RAIN_ABOVE, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 2.5F, 0.45F);
   }

   public static void rumbles(ServerLevel level, Vec3 at, int age) {
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 2.0F + (float)age * 0.05F, 0.5F + (float)age * 0.006F);
   }

   public static void emerges(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 6.0F, 0.6F);
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 5.0F, 0.8F);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 120, 3.0, 1.0, 3.0, 0.6);
   }

   public static void crackles(ServerLevel level, Vec3 at, int age) {
      GsFx.play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 3.0F, 1.6F);
      if (age % 60 == 0) {
         GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 4.0F, 0.7F);
      }
   }

   public static void strikes(ServerLevel level, Vec3 at, int n) {
      float climb = (float)n / 12.0F;
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 4.0F, 0.7F + climb * 0.5F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 4.0F, 0.6F + climb * 0.5F);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y + 0.5, at.z, 60 + n * 6, 1.5, 0.6, 1.5, 0.8);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 1.0, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 0.3, at.z, 12, 1.2, 0.2, 1.2, 0.03);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 8, at, 20.0F + (float)n));
   }

   public static void coils(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 4.0F, 0.6F);
   }

   public static void climbs(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 8.0F, 0.55F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 6.0F, 0.5F);
   }

   public static void dives(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:item.trident.riptide_3", SoundEvents.TRIDENT_RIPTIDE_3, 8.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 8.0F, 0.8F);
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 8.0F, 0.6F);
   }

   public static void impact(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 10.0F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 10.0F, 0.55F);
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 10.0F, 0.45F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 10.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.death", SoundEvents.ENDER_DRAGON_DEATH, 3.0F, 1.8F);

      for (int ring = 0; ring < 3; ring++) {
         double r = (double)(6 + ring * 9);

         for (int i = 0; i < 10; i++) {
            double a = (Math.PI * 2) * (double)i / 10.0 + (double)ring;
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x + Math.cos(a) * r, at.y + 1.0, at.z + Math.sin(a) * r, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }

      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y + 3.0, at.z, 900, 14.0, 6.0, 14.0, 2.0);
      level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 2.0, at.z, 300, 18.0, 3.0, 18.0, 0.15);
      level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, at.x, at.y + 1.0, at.z, 80, 10.0, 1.0, 10.0, 0.05);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 4.0, at.z, 6, 4.0, 4.0, 4.0, 0.0);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 60, at, 120.0F));
   }

   public static void pours(ServerLevel level, Vec3 at, int tick) {
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 4.0F, 0.6F + (float)(tick % 20) * 0.02F);
      if (tick % 10 == 0) {
         GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 3.0F, 0.7F);
      }

      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y + 2.0, at.z, 80, 4.0, 3.0, 4.0, 0.9);
      level.sendParticles(ParticleTypes.EXPLOSION, at.x, at.y + 1.0, at.z, 3, 4.0, 1.0, 4.0, 0.0);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 6, at, 40.0F));
   }

   public static void finale(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 8.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.impact", SoundEvents.LIGHTNING_BOLT_IMPACT, 8.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 6.0F, 0.6F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.death", SoundEvents.ENDER_DRAGON_DEATH, 2.0F, 1.6F);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 1.0, at.z, 3, 3.0, 1.0, 3.0, 0.0);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y + 2.0, at.z, 400, 6.0, 4.0, 6.0, 1.2);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 2.0, at.z, 3, 2.0, 2.0, 2.0, 0.0);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 36, at, 56.0F));
   }
}
