package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class GateFx {
   private GateFx() {
   }

   public static void opens(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 4.0F, 0.7F);
      GsFx.play(level, at, "minecraft:block.conduit.activate", SoundEvents.CONDUIT_ACTIVATE, 3.5F, 0.9F);
      GsFx.play(level, at, "minecraft:block.end_portal.spawn", SoundEvents.WITHER_SPAWN, 2.5F, 1.4F);
      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, (float)radius));
   }

   public static void unfolding(ServerLevel level, Vec3 at, float progress) {
      GsFx.play(level, at, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 2.0F, 0.9F + progress * 0.9F);
      GsFx.play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 1.4F, 0.8F + progress * 0.8F);
   }

   public static void choir(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 2.6F, 0.8F);
      GsFx.play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 1.8F, 1.2F);
      GsFx.play(level, at, "minecraft:block.conduit.ambient", SoundEvents.CONDUIT_AMBIENT, 1.4F, 1.0F);
      int points = (int)Math.max(48.0, radius * 3.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(
            IronParticles.or("irons_spellbooks:cleanse", ParticleTypes.END_ROD),
            at.x + Math.cos(a) * radius,
            at.y + 0.3,
            at.z + Math.sin(a) * radius,
            1,
            0.1,
            0.3,
            0.1,
            0.02
         );
      }

      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
   }

   public static void mended(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(IronParticles.or("irons_spellbooks:cleanse", ParticleTypes.END_ROD), at.x, at.y, at.z, 6, 0.3, 0.4, 0.3, 0.02);
   }

   public static void closes(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 3.5F, 0.8F);
      GsFx.play(level, at, "minecraft:block.conduit.deactivate", SoundEvents.CONDUIT_ACTIVATE, 2.5F, 0.7F);

      for (int i = 0; i < 60; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius;
         level.sendParticles(
            IronParticles.or("irons_spellbooks:falling_sparkle", ParticleTypes.END_ROD),
            at.x + Math.cos(a) * rr,
            at.y + 8.0 + level.random.nextDouble() * 20.0,
            at.z + Math.sin(a) * rr,
            1,
            0.2,
            0.2,
            0.2,
            0.01
         );
      }
   }
}
