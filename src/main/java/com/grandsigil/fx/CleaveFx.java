package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class CleaveFx {
   private CleaveFx() {
   }

   public static void focuses(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.respawn_anchor.charge", SoundEvents.RESPAWN_ANCHOR_CHARGE, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 4.0F, 1.6F);
   }

   public static void cuts(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.player.attack.sweep", SoundEvents.PLAYER_ATTACK_SWEEP, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 6.0F, 1.4F);
      GsFx.play(level, at, "minecraft:block.glass.break", SoundEvents.GLASS_BREAK, 5.0F, 1.6F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 12, at, 70.0F));
   }

   public static void tears(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.enderman.teleport", SoundEvents.ENDERMAN_TELEPORT, 6.0F, 0.4F);
      GsFx.play(level, at, "minecraft:block.end_portal.spawn", SoundEvents.END_PORTAL_SPAWN, 4.0F, 0.8F);
      level.sendParticles(ParticleTypes.REVERSE_PORTAL, at.x, at.y, at.z, 200, 16.0, 6.0, 16.0, 0.4);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, 60.0F));
   }

   public static void hums(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.portal.ambient", SoundEvents.PORTAL_AMBIENT, 5.0F, 0.5F);
   }

   public static void snaps(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 6.0F, 0.6F);
      GsFx.play(level, at, "minecraft:block.end_portal_frame.fill", SoundEvents.END_PORTAL_FRAME_FILL, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:entity.player.attack.sweep", SoundEvents.PLAYER_ATTACK_SWEEP, 6.0F, 0.7F);
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 200, 20.0, 6.0, 20.0, 0.5);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, at, 70.0F));
   }
}
