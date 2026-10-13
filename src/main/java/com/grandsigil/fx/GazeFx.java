package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class GazeFx {
   private static final String ARRIVAL = "irons_spellbooks:cast.void_tentacles.start";
   private static final String DEPARTURE = "irons_spellbooks:entity.void_tentacles.retreat";

   private GazeFx() {
   }

   public static void opens(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.warden.emerge", SoundEvents.WARDEN_EMERGE, 4.0F, 0.4F);
      GsFx.play(level, at, "minecraft:block.portal.trigger", SoundEvents.PORTAL_TRIGGER, 3.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.elder_guardian.curse", SoundEvents.ELDER_GUARDIAN_CURSE, 2.5F, 0.6F);
      GsFx.play(level, at, "irons_spellbooks:cast.void_tentacles.start", SoundEvents.SCULK_CATALYST_BLOOM, 3.0F, 0.7F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 40, at, (float)radius));
      level.sendParticles(
         IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL), at.x, at.y + 12.0, at.z, 120, radius * 0.4, 8.0, radius * 0.4, 0.6
      );
   }

   public static void tearing(ServerLevel level, Vec3 at, float progress) {
      GsFx.play(level, at, "minecraft:entity.warden.heartbeat", SoundEvents.WARDEN_HEARTBEAT, 3.0F, 0.5F + progress * 0.3F);
      GsFx.play(level, at, "minecraft:block.sculk_shrieker.shriek", SoundEvents.SCULK_SHRIEKER_SHRIEK, 1.6F, 0.4F + progress * 0.4F);
   }

   public static void watching(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.warden.heartbeat", SoundEvents.WARDEN_HEARTBEAT, 2.6F, 0.6F);
      GsFx.play(level, at, "minecraft:entity.warden.listening", SoundEvents.WARDEN_LISTENING, 2.2F, 0.7F);
      int points = (int)Math.max(40.0, radius * 2.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(
            IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL),
            at.x + Math.cos(a) * radius,
            at.y + 0.5,
            at.z + Math.sin(a) * radius,
            1,
            0.1,
            0.4,
            0.1,
            0.04
         );
      }

      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
   }

   public static void caught(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      GsFx.play(level, at, "minecraft:entity.warden.sonic_charge", SoundEvents.WARDEN_SONIC_CHARGE, 1.6F, 1.3F);
      level.sendParticles(IronParticles.or("irons_spellbooks:trial_omen", ParticleTypes.PORTAL), at.x, at.y + 0.6, at.z, 24, 0.8, 0.9, 0.8, 0.12);
      level.sendParticles(ParticleTypes.SCULK_SOUL, at.x, at.y + 0.9, at.z, 6, 0.4, 0.5, 0.4, 0.02);
   }

   public static void stares(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL), at.x, at.y, at.z, 8, 0.4, 0.6, 0.4, 0.15);
      level.sendParticles(ParticleTypes.SCULK_SOUL, at.x, at.y + 1.0, at.z, 2, 0.3, 0.3, 0.3, 0.02);
   }

   public static void blinks(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.warden.heartbeat", SoundEvents.WARDEN_HEARTBEAT, 2.2F, 0.8F);
   }

   public static void noticed(ServerLevel level, Entity caster) {
      Vec3 at = caster.getBoundingBox().getCenter();
      GsFx.play(level, at, "minecraft:entity.warden.nearby_closest", SoundEvents.WARDEN_NEARBY_CLOSEST, 2.0F, 0.6F);
      level.sendParticles(ParticleTypes.SCULK_SOUL, at.x, at.y + 1.2, at.z, 20, 0.4, 0.5, 0.4, 0.05);
   }

   public static void releases(ServerLevel level, Entity target) {
      Vec3 at = target.position();
      GsFx.play(level, at, "irons_spellbooks:entity.void_tentacles.retreat", SoundEvents.SCULK_CATALYST_BLOOM, 1.4F, 1.2F);
      level.sendParticles(ParticleTypes.SCULK_SOUL, at.x, at.y + 1.0, at.z, 18, 0.5, 0.8, 0.5, 0.06);

      for (int i = 0; i < 20; i++) {
         double a = (Math.PI * 2) * (double)i / 20.0;
         level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, at.x + Math.cos(a) * 1.2, at.y + 1.0, at.z + Math.sin(a) * 1.2, 1, 0.1, 0.3, 0.1, 0.06);
      }
   }

   public static void closes(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.warden.dig", SoundEvents.WARDEN_DIG, 3.5F, 0.5F);
      GsFx.play(level, at, "minecraft:block.sculk_shrieker.shriek", SoundEvents.SCULK_SHRIEKER_SHRIEK, 2.0F, 0.3F);
      GsFx.play(level, at, "irons_spellbooks:entity.void_tentacles.retreat", SoundEvents.SCULK_CATALYST_BLOOM, 2.5F, 0.7F);
      level.sendParticles(
         IronParticles.or("irons_spellbooks:unstable_ender", ParticleTypes.PORTAL), at.x, at.y + 10.0, at.z, 90, radius * 0.35, 6.0, radius * 0.35, 0.4
      );
   }
}
