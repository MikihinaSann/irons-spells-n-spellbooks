package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class GripFx {
   private GripFx() {
   }

   public static void opens(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.portal.trigger", SoundEvents.PORTAL_TRIGGER, 3.5F, 0.45F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.end_portal.spawn", SoundEvents.END_PORTAL_SPAWN, 2.5F, 1.3F);
      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 26, at, (float)radius));
      level.sendParticles(
         IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL), at.x, at.y + 30.0, at.z, 200, radius * 0.5, 18.0, radius * 0.5, 0.5
      );
   }

   public static void arriving(ServerLevel level, Vec3 at, double radius, float progress) {
      GsFx.play(level, at, "minecraft:block.end_portal_frame.fill", SoundEvents.END_PORTAL_FRAME_FILL, 3.0F, 0.35F + progress * 0.2F);
      GsFx.play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 2.0F, 0.4F + progress * 0.9F);
      level.sendParticles(ParticleTypes.REVERSE_PORTAL, at.x, at.y + 2.0, at.z, 80, radius * 0.4, 6.0, radius * 0.4, 0.4);
   }

   public static void takes(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      GsFx.play(level, at, "minecraft:entity.enderman.teleport", SoundEvents.ENDERMAN_TELEPORT, 1.8F, 0.5F);
      level.sendParticles(IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL), at.x, at.y, at.z, 60, 0.6, 0.9, 0.6, 0.4);
   }

   public static void closed(ServerLevel level, Vec3 at, int held) {
      GsFx.play(level, at, "minecraft:entity.warden.sonic_charge", SoundEvents.WARDEN_SONIC_CHARGE, 3.5F, 0.4F);
      GsFx.play(level, at, "minecraft:block.conduit.activate", SoundEvents.CONDUIT_ACTIVATE, 2.5F + Math.min(2.0F, (float)held * 0.2F), 0.35F);
   }

   public static void straining(ServerLevel level, Vec3 at, double radius, int held) {
      float pitch = 0.3F + Math.min(0.35F, (float)held * 0.03F);
      GsFx.play(level, at, "minecraft:block.portal.ambient", SoundEvents.PORTAL_AMBIENT, 3.0F, pitch);
      GsFx.play(level, at, "minecraft:entity.elder_guardian.ambient", SoundEvents.ELDER_GUARDIAN_AMBIENT, 1.6F, pitch);
   }

   public static void drives(ServerLevel level, Vec3 at, double radius, int held) {
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.flap", SoundEvents.ENDER_DRAGON_FLAP, 4.0F, 0.35F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 12, at, (float)radius));
   }

   public static void lands(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.wither.break_block", SoundEvents.WITHER_BREAK_BLOCK, 3.0F, 0.45F);
      GsFx.play(level, at, "minecraft:entity.generic.big_fall", SoundEvents.GENERIC_BIG_FALL, 2.5F, 0.5F);
      level.sendParticles(ParticleTypes.EXPLOSION, at.x, at.y + 0.3, at.z, 6, 1.2, 0.3, 1.2, 0.0);
      level.sendParticles(ParticleTypes.POOF, at.x, at.y + 0.2, at.z, 40, 1.6, 0.2, 1.6, 0.3);
   }

   public static void crater(ServerLevel level, Vec3 at, double radius, int slammed) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 5.0F, 0.3F);
      GsFx.play(level, at, "minecraft:block.deepslate.break", SoundEvents.DEEPSLATE_BREAK, 5.0F, 0.28F);
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 5.0F, 0.35F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 44, at, (float)radius * 1.6F));
      level.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y + 1.0, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 1.0, at.z, 3, radius * 0.35, 1.0, radius * 0.35, 0.0);
      int spokes = (int)Math.max(48.0, radius * 3.0);

      for (int i = 0; i < spokes; i++) {
         double a = (Math.PI * 2) * (double)i / (double)spokes;
         level.sendParticles(ParticleTypes.POOF, at.x + Math.cos(a) * 2.0, at.y + 0.6, at.z + Math.sin(a) * 2.0, 0, Math.cos(a), 0.08, Math.sin(a), 1.5);
      }

      level.sendParticles(
         IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL), at.x, at.y + 2.0, at.z, 220, radius * 0.4, 3.0, radius * 0.4, 0.6
      );
      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
   }
}
