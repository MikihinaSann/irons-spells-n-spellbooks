package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SunFx {
   private SunFx() {
   }

   public static void taken(ServerLevel level, Vec3 ground) {
      GsFx.play(level, ground, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 4.0F, 0.35F);
      GsFx.play(level, ground, "minecraft:entity.wither.spawn", SoundEvents.WITHER_SPAWN, 2.5F, 0.4F);
      GsFx.play(level, ground, "minecraft:item.firecharge.use", SoundEvents.FIRECHARGE_USE, 3.0F, 0.4F);
   }

   public static void climbing(ServerLevel level, Vec3 ground, float progress) {
      GsFx.play(level, ground, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 1.8F, 0.5F + progress * 0.9F);
      GsFx.play(level, ground, "minecraft:block.lava.ambient", SoundEvents.LAVA_AMBIENT, 2.0F, 0.4F + progress * 0.4F);
   }

   public static void noon(ServerLevel level, Vec3 ground, double radius) {
      GsFx.play(level, ground, "minecraft:block.conduit.activate", SoundEvents.CONDUIT_ACTIVATE, 4.0F, 0.6F);
      GsFx.play(level, ground, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 5.0F, 0.3F);
      GsFx.play(level, ground, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 3.0F, 1.6F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 40, ground, (float)(radius * 2.0)));
   }

   public static void charging(ServerLevel level, Vec3 ground, double radius, float progress) {
      GsFx.play(level, ground, "minecraft:block.fire.ambient", SoundEvents.FIRE_AMBIENT, 3.5F, 0.4F + progress * 0.4F);
      GsFx.play(level, ground, "minecraft:entity.blaze.ambient", SoundEvents.BLAZE_AMBIENT, 2.0F, 0.5F + progress * 0.7F);
      int count = 8 + Mth.floor(progress * 28.0F);

      for (int i = 0; i < count; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius;
         level.sendParticles(ParticleTypes.SMOKE, ground.x + Math.cos(a) * rr, ground.y + 0.2, ground.z + Math.sin(a) * rr, 1, 0.2, 0.1, 0.2, 0.02);
      }
   }

   public static void fired(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 8.0F, 0.35F);
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 8.0F, 0.45F);
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 6.0F, 0.4F);
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 4.0F, 0.6F);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 2.0, at.z, 8, 1.0, 2.0, 1.0, 0.0);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 1.0, at.z, 3, 5.0, 1.0, 5.0, 0.0);
      level.sendParticles(ParticleTypes.LAVA, at.x, at.y + 1.0, at.z, 110, 5.0, 2.0, 5.0, 0.6);
      int points = 120;

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(ParticleTypes.FLAME, at.x + Math.cos(a) * radius, at.y + 0.4, at.z + Math.sin(a) * radius, 2, 0.3, 0.4, 0.3, 0.06);
      }

      CameraShakeManager.addCameraShake(new CameraShakeData(level, 60, at, (float)(radius * 3.0)));
   }

   public static void aftermath(ServerLevel level, Vec3 at, double radius, int tick) {
      if (tick == 20) {
         GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 9.0F, 0.3F);
      }

      if (tick % 4 == 0) {
         GsFx.play(level, at, "minecraft:block.fire.ambient", SoundEvents.FIRE_AMBIENT, 3.0F, 0.5F);
      }

      double travelled = radius * (0.3 + (double)tick * 0.09);
      int points = (int)Mth.clamp(travelled * 2.2, 24.0, 150.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(ParticleTypes.FLAME, at.x + Math.cos(a) * travelled, at.y + 0.3, at.z + Math.sin(a) * travelled, 1, 0.1, 0.2, 0.1, 0.04);
      }

      for (int i = 0; i < 10; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius;
         level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x + Math.cos(a) * rr, at.y + 0.5, at.z + Math.sin(a) * rr, 1, 0.3, 0.3, 0.3, 0.05);
      }
   }
}
