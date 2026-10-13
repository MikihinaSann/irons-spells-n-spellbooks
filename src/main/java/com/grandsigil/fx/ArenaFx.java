package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ArenaFx {
   private ArenaFx() {
   }

   public static void erupts(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.deepslate.break", SoundEvents.DEEPSLATE_BREAK, 5.0F, 0.3F);
      GsFx.play(level, at, "minecraft:block.sculk.break", SoundEvents.SCULK_BLOCK_BREAK, 4.0F, 0.35F);
      GsFx.play(level, at, "minecraft:entity.wither.spawn", SoundEvents.WITHER_SPAWN, 2.5F, 0.6F);
      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 40, at, (float)radius * 1.3F));
      int points = (int)Math.max(72.0, radius * 5.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(ParticleTypes.POOF, at.x + Math.cos(a) * radius, at.y + 0.5, at.z + Math.sin(a) * radius, 3, 0.4, 0.6, 0.4, 0.1);
      }
   }

   public static void building(ServerLevel level, Vec3 at, double radius, float progress) {
      GsFx.play(level, at, "minecraft:block.sculk.place", SoundEvents.SCULK_BLOCK_PLACE, 3.5F, 0.35F + progress * 0.25F);
      GsFx.play(level, at, "minecraft:block.deepslate.place", SoundEvents.DEEPSLATE_PLACE, 3.0F, 0.3F + progress * 0.2F);
      int points = (int)Math.max(60.0, radius * 4.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(
            ParticleTypes.POOF, at.x + Math.cos(a) * radius, at.y + (double)progress * 40.0, at.z + Math.sin(a) * radius, 1, 0.5, 0.5, 0.5, 0.05
         );
      }
   }

   public static void roaring(ServerLevel level, Vec3 at, double radius, int killed) {
      float pitch = 0.4F + Math.min(0.5F, (float)killed * 0.045F);
      GsFx.play(level, at, "minecraft:entity.ravager.roar", SoundEvents.RAVAGER_ROAR, 3.0F, pitch);
      GsFx.play(level, at, "minecraft:ambient.cave", SoundEvents.AMBIENT_CAVE.value(), 2.5F, pitch);
      GsFx.play(level, at, "minecraft:entity.warden.heartbeat", SoundEvents.WARDEN_HEARTBEAT, 2.4F, pitch * 0.8F);
   }

   public static void pouring(ServerLevel level, Vec3 at, double radius, int struck) {
      float pitch = 0.45F + level.random.nextFloat() * 0.35F;
      float volume = 1.5F + Math.min(2.5F, (float)struck * 0.5F);
      double a = level.random.nextDouble() * Math.PI * 2.0;
      double rr = Math.sqrt(level.random.nextDouble()) * radius * 0.8;
      Vec3 where = new Vec3(at.x + Math.cos(a) * rr, at.y, at.z + Math.sin(a) * rr);
      GsFx.play(level, where, "minecraft:item.trident.hit_ground", SoundEvents.TRIDENT_HIT_GROUND, volume, pitch);
      if (level.random.nextInt(2) == 0) {
         GsFx.play(level, where, "minecraft:item.trident.throw", SoundEvents.TRIDENT_THROW, volume * 0.7F, pitch * 0.9F);
      }

      level.sendParticles(IronParticles.or("irons_spellbooks:blood", ParticleTypes.DAMAGE_INDICATOR), where.x, where.y + 0.4, where.z, 8, 0.4, 0.3, 0.4, 0.2);
   }

   public static void repelled(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.sculk.hit", SoundEvents.SCULK_BLOCK_HIT, 1.6F, 0.45F);
      level.sendParticles(IronParticles.or("irons_spellbooks:blood", ParticleTypes.DAMAGE_INDICATOR), at.x, at.y + 1.0, at.z, 12, 0.3, 0.5, 0.3, 0.2);
   }

   public static void bled(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(IronParticles.or("irons_spellbooks:blood", ParticleTypes.DAMAGE_INDICATOR), at.x, at.y, at.z, 10, 0.35, 0.5, 0.35, 0.08);
   }

   public static void fed(ServerLevel level, Vec3 at, int killed) {
      GsFx.play(level, at, "minecraft:block.sculk.place", SoundEvents.SCULK_BLOCK_PLACE, 3.0F, 0.45F);
      GsFx.play(level, at, "minecraft:entity.ravager.celebrate", SoundEvents.RAVAGER_CELEBRATE, 2.6F, 0.7F);
      level.sendParticles(IronParticles.or("irons_spellbooks:blood", ParticleTypes.DAMAGE_INDICATOR), at.x, at.y + 1.0, at.z, 60, 0.5, 0.8, 0.5, 0.6);
   }

   public static void collapses(ServerLevel level, Vec3 at, double radius, float height) {
      GsFx.play(level, at, "minecraft:block.sculk.break", SoundEvents.SCULK_BLOCK_BREAK, 5.0F, 0.28F);
      GsFx.play(level, at, "minecraft:entity.ravager.roar", SoundEvents.RAVAGER_ROAR, 4.0F, 0.4F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 60, at, (float)radius * 1.5F));

      for (int i = 0; i < 200; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         level.sendParticles(
            ParticleTypes.POOF,
            at.x + Math.cos(a) * radius,
            at.y + level.random.nextDouble() * (double)height,
            at.z + Math.sin(a) * radius,
            1,
            0.6,
            0.6,
            0.6,
            0.2
         );
      }
   }

   public static void buried(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 5.0F, 0.28F);
      GsFx.play(level, at, "minecraft:block.deepslate.break", SoundEvents.DEEPSLATE_BREAK, 5.0F, 0.25F);
      GsFx.play(level, at, "minecraft:block.anvil.land", SoundEvents.ANVIL_LAND, 4.0F, 0.3F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 40, at, (float)radius * 1.7F));
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 2.0, at.z, 3, radius * 0.4, 1.0, radius * 0.4, 0.0);
      level.sendParticles(
         IronParticles.or("irons_spellbooks:blood", ParticleTypes.DAMAGE_INDICATOR), at.x, at.y + 3.0, at.z, 300, radius * 0.5, 4.0, radius * 0.5, 0.8
      );
      int spokes = (int)Math.max(64.0, radius * 4.0);

      for (int i = 0; i < spokes; i++) {
         double a = (Math.PI * 2) * (double)i / (double)spokes;
         level.sendParticles(ParticleTypes.POOF, at.x + Math.cos(a) * 2.0, at.y + 0.8, at.z + Math.sin(a) * 2.0, 0, Math.cos(a), 0.1, Math.sin(a), 1.7);
      }

      IronParticles.flatRing(level, at.x, at.y + 0.15, at.z);
   }
}
