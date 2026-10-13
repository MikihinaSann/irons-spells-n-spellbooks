package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class ArsenalFx {
   private ArsenalFx() {
   }

   public static void opens(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.enchantment_table.use", SoundEvents.ENCHANTMENT_TABLE_USE, 2.6F, 0.6F);
      GsFx.play(level, at, "minecraft:entity.illusioner.prepare_mirror", SoundEvents.ILLUSIONER_PREPARE_MIRROR, 2.2F, 0.75F);
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 2.0F, 1.3F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 2.0F, 0.8F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 14, at, 24.0F));
   }

   public static void launched(ServerLevel level, Vec3 gate, int index) {
      float climb = (float)index / 120.0F;
      float wobble = (float)(index * 37 % 7) / 7.0F * 0.12F;
      GsFx.play(level, gate, "minecraft:item.trident.throw", SoundEvents.TRIDENT_THROW, 1.1F, 0.85F + climb * 0.55F + wobble);
      if (index % 3 == 0) {
         GsFx.play(level, gate, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 0.9F, 1.0F + climb * 0.8F);
      }

      level.sendParticles(ParticleTypes.END_ROD, gate.x, gate.y, gate.z, 4, 0.18, 0.18, 0.18, 0.06);
   }

   public static void hit(ServerLevel level, Vec3 at, int index) {
      GsFx.play(level, at, "minecraft:item.trident.hit", SoundEvents.TRIDENT_HIT, 1.2F, 0.9F + (float)(index % 5) * 0.06F);
      if (index % 3 == 0) {
         GsFx.play(level, at, "minecraft:entity.player.attack.crit", SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 1.1F);
      }

      level.sendParticles(ParticleTypes.CRIT, at.x, at.y, at.z, 8, 0.15, 0.15, 0.15, 0.35);
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 5, 0.1, 0.1, 0.1, 0.12);
   }

   public static void ground(ServerLevel level, Vec3 at, int index) {
      GsFx.play(level, at, "minecraft:item.trident.hit_ground", SoundEvents.TRIDENT_HIT_GROUND, 1.3F, 0.8F + (float)(index % 4) * 0.07F);
      level.sendParticles(ParticleTypes.CRIT, at.x, at.y + 0.1, at.z, 6, 0.2, 0.05, 0.2, 0.2);
   }

   public static void grandOpens(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.enchantment_table.use", SoundEvents.ENCHANTMENT_TABLE_USE, 4.0F, 0.4F);
      GsFx.play(level, at, "minecraft:block.end_portal.spawn", SoundEvents.END_PORTAL_SPAWN, 2.5F, 1.4F);
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 80, 5.0, 0.4, 5.0, 0.08);
   }

   public static void drawsColossus(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:item.trident.riptide_3", SoundEvents.TRIDENT_RIPTIDE_3, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 4.0F, 0.5F);
   }

   public static void colossus(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 6.0F, 0.5F);
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 5.0F, 0.7F);
      GsFx.play(level, at, "minecraft:item.trident.hit_ground", SoundEvents.TRIDENT_HIT_GROUND, 5.0F, 0.4F);
      level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y + 1.0, at.z, 3, 3.0, 0.5, 3.0, 0.0);
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y + 1.0, at.z, 220, 4.0, 1.5, 4.0, 0.45);
      level.sendParticles(ParticleTypes.CRIT, at.x, at.y + 0.5, at.z, 160, 6.0, 0.5, 6.0, 0.7);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, 48.0F));
   }

   public static void closes(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 1.8F, 1.2F);
      GsFx.play(level, at, "minecraft:block.enchantment_table.use", SoundEvents.ENCHANTMENT_TABLE_USE, 1.6F, 1.4F);
   }
}
