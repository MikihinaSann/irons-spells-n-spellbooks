package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class PhoenixFx {
   private PhoenixFx() {
   }

   public static void ignites(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.blaze.shoot", SoundEvents.BLAZE_SHOOT, 2.4F, 0.55F);
      GsFx.play(level, at, "minecraft:item.firecharge.use", SoundEvents.FIRECHARGE_USE, 2.2F, 0.6F);
      GsFx.play(level, at, "minecraft:block.respawn_anchor.charge", SoundEvents.RESPAWN_ANCHOR_CHARGE, 2.0F, 0.7F);
      level.sendParticles(ParticleTypes.FLAME, at.x, at.y, at.z, 60, 0.6, 0.2, 0.6, 0.12);
      level.sendParticles(ParticleTypes.LAVA, at.x, at.y, at.z, 12, 0.6, 0.2, 0.6, 0.1);
   }

   public static void cries(ServerLevel level, Vec3 at, float lift) {
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 3.5F, 1.6F * lift);
      GsFx.play(level, at, "minecraft:entity.blaze.ambient", SoundEvents.BLAZE_AMBIENT, 3.0F, 0.5F * lift);
   }

   public static void beats(ServerLevel level, Vec3 at, int age) {
      GsFx.play(level, at, "minecraft:entity.ender_dragon.flap", SoundEvents.ENDER_DRAGON_FLAP, 2.6F, 0.75F + (float)(age / 18 % 3) * 0.05F);
      GsFx.play(level, at, "minecraft:entity.blaze.burn", SoundEvents.BLAZE_BURN, 1.6F, 0.6F);
   }

   public static void roars(ServerLevel level, Vec3 at, int age) {
      GsFx.play(level, at, "minecraft:block.fire.ambient", SoundEvents.FIRE_AMBIENT, 3.0F, 0.5F + (float)(age % 5) * 0.04F);
      if (age % 36 == 0) {
         GsFx.play(level, at, "minecraft:block.lava.ambient", SoundEvents.LAVA_AMBIENT, 2.4F, 0.6F);
      }
   }

   public static void dives(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 4.0F, 1.9F);
      GsFx.play(level, at, "minecraft:item.trident.riptide_3", SoundEvents.TRIDENT_RIPTIDE_3, 3.0F, 0.7F);
      GsFx.play(level, at, "minecraft:entity.ghast.shoot", SoundEvents.GHAST_SHOOT, 3.0F, 0.5F);
   }

   public static void bursts(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 5.0F, 0.55F);
      GsFx.play(level, at, "minecraft:entity.dragon_fireball.explode", SoundEvents.DRAGON_FIREBALL_EXPLODE, 4.0F, 0.6F);
      GsFx.play(level, at, "minecraft:item.firecharge.use", SoundEvents.FIRECHARGE_USE, 4.0F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.blaze.death", SoundEvents.BLAZE_DEATH, 3.0F, 0.5F);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 1.0, at.z, 2, 1.5, 0.5, 1.5, 0.0);
      level.sendParticles(ParticleTypes.FLAME, at.x, at.y + 0.5, at.z, 260, 2.5, 0.8, 2.5, 0.45);
      level.sendParticles(ParticleTypes.LAVA, at.x, at.y + 0.5, at.z, 60, 3.0, 0.5, 3.0, 0.2);
      level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 1.0, at.z, 80, 4.0, 1.0, 4.0, 0.08);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, 40.0F));
   }

   public static void reborn(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:item.totem.use", SoundEvents.TOTEM_USE, 3.0F, 0.65F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 1.4F);
   }
}
