package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class IceAgeFx {
   private IceAgeFx() {
   }

   public static void gathers(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 5.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.player.hurt_freeze", SoundEvents.PLAYER_HURT_FREEZE, 4.0F, 0.5F);
   }

   public static void descends(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:item.elytra.flying", SoundEvents.ELYTRA_FLYING, 6.0F, 0.6F);
      GsFx.play(level, at, "minecraft:block.powder_snow.break", SoundEvents.POWDER_SNOW_BREAK, 5.0F, 0.5F);
      level.sendParticles(ParticleTypes.SNOWFLAKE, at.x, at.y + 2.0, at.z, 200, 12.0, 2.0, 12.0, 0.2);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, 40.0F));
   }

   public static void howls(ServerLevel level, Vec3 at, int age) {
      GsFx.play(level, at, "minecraft:item.elytra.flying", SoundEvents.ELYTRA_FLYING, 5.0F, 0.5F + (float)(age % 80) * 0.003F);
   }

   public static void lands(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 3.0F, 0.6F);
      GsFx.play(level, at, "minecraft:block.amethyst_cluster.break", SoundEvents.AMETHYST_CLUSTER_BREAK, 3.0F, 0.7F);
      level.sendParticles(ParticleTypes.SNOWFLAKE, at.x, at.y + 0.5, at.z, 40, 2.0, 0.6, 2.0, 0.25);
      level.sendParticles(ParticleTypes.ITEM_SNOWBALL, at.x, at.y + 0.5, at.z, 24, 1.5, 0.5, 1.5, 0.3);
   }

   public static void stills(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.conduit.deactivate", SoundEvents.CONDUIT_DEACTIVATE, 6.0F, 0.6F);
      GsFx.play(level, at, "minecraft:block.bell.resonate", SoundEvents.BELL_RESONATE, 6.0F, 0.6F);
   }

   public static void shatters(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 6.0F, 0.7F);
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.break", SoundEvents.AMETHYST_BLOCK_BREAK, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 5.0F, 0.8F);
      level.sendParticles(ParticleTypes.SNOWFLAKE, at.x, at.y + 3.0, at.z, 500, 24.0, 4.0, 24.0, 0.6);
      level.sendParticles(ParticleTypes.ITEM_SNOWBALL, at.x, at.y + 2.0, at.z, 300, 20.0, 3.0, 20.0, 0.5);
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y + 4.0, at.z, 200, 16.0, 6.0, 16.0, 0.4);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, 80.0F));
   }
}
