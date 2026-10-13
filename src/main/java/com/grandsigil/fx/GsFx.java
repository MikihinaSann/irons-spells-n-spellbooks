package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GsFx {
   public static final Vector3f GOLD = new Vector3f(1.0F, 0.8F, 0.32F);
   public static final Vector3f WHITE = new Vector3f(1.0F, 0.97F, 0.86F);
   public static final Vector3f VIOLET = new Vector3f(0.6F, 0.42F, 1.0F);
   private static final Map<String, SoundEvent> SOUNDS = new HashMap<>();

   private GsFx() {
   }

   public static SoundEvent sound(String id, Holder<SoundEvent> fallback) {
      SoundEvent cached = SOUNDS.get(id);
      if (cached != null) {
         return cached;
      } else {
         SoundEvent found = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(id));
         SoundEvent resolved = found != null ? found : fallback.value();
         SOUNDS.put(id, resolved);
         return resolved;
      }
   }

   public static void play(ServerLevel level, Vec3 at, String id, SoundEvent fallback, float volume, float pitch) {
      level.playSound(null, at.x, at.y, at.z, sound(id, fallback), SoundSource.PLAYERS, volume, pitch);
   }

   public static SoundEvent sound(String id, SoundEvent fallback) {
      SoundEvent cached = SOUNDS.get(id);
      if (cached != null) {
         return cached;
      } else {
         SoundEvent found = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(id));
         SoundEvent resolved = found != null ? found : fallback;
         SOUNDS.put(id, resolved);
         return resolved;
      }
   }

   public static void play(ServerLevel level, Vec3 at, String id, Holder<SoundEvent> fallback, float volume, float pitch) {
      level.playSound(null, at.x, at.y, at.z, sound(id, fallback), SoundSource.PLAYERS, volume, pitch);
   }

   public static void circleDrawn(ServerLevel level, Vec3 at, double radius) {
      play(level, at, "minecraft:block.enchantment_table.use", SoundEvents.ENCHANTMENT_TABLE_USE, 2.4F, 0.6F);
      play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 1.6F, 0.7F);
      play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 1.8F, 0.5F);
      play(level, at, "minecraft:entity.illusioner.prepare_mirror", SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.4F, 0.7F);
      ring(level, at, radius, (int)Math.max(40.0, radius * 4.0), 0.15);
   }

   public static void circleHum(ServerLevel level, Vec3 at, float fill) {
      play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 0.9F, 0.55F + fill * 0.5F);
      play(level, at, "minecraft:block.conduit.ambient", SoundEvents.CONDUIT_AMBIENT, 0.5F, 0.6F + fill * 0.4F);
   }

   public static void runeLit(ServerLevel level, Vec3 at, double radius, int index, int total, float pitch) {
      double a = (Math.PI * 2) * (double)index / (double)total;
      double rr = radius * 0.62;
      Vec3 seat = at.add(Math.cos(a) * rr, 0.05, Math.sin(a) * rr);
      play(level, seat, "minecraft:block.note_block.chime", SoundEvents.NOTE_BLOCK_CHIME.value(), 1.6F, pitch);
      play(level, seat, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, pitch);
      play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 0.5F, pitch);
      level.sendParticles(ParticleTypes.END_ROD, seat.x, seat.y, seat.z, 18, 0.08, 0.02, 0.08, 0.35);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, seat.x, seat.y + 0.3, seat.z, 12, 0.15, 0.25, 0.15, 0.12);
      level.sendParticles(ParticleTypes.FLASH, seat.x, seat.y + 0.6, seat.z, 1, 0.0, 0.0, 0.0, 0.0);
   }

   public static void ignition(ServerLevel level, Vec3 at, double radius, boolean perfect) {
      play(level, at, "minecraft:entity.evoker.prepare_summon", SoundEvents.EVOKER_PREPARE_SUMMON, 3.0F, 0.65F);
      play(level, at, "minecraft:block.conduit.activate", SoundEvents.CONDUIT_ACTIVATE, 2.2F, 0.8F);
      play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 2.0F, perfect ? 1.4F : 1.0F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 30, at, (float)(radius * 4.0)));
   }

   public static void ignitionStep(ServerLevel level, Vec3 at, double radius, int tick, int total) {
      float progress = (float)tick / (float)total;
      if (tick % 4 == 0) {
         play(level, at, "minecraft:block.note_block.bell", SoundEvents.NOTE_BLOCK_BELL.value(), 0.7F, 0.7F + progress * 1.2F);
      }

      int count = 6 + (int)(progress * 10.0F);

      for (int i = 0; i < count; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = radius * (0.6 + level.random.nextDouble() * 0.45);
         double x = at.x + Math.cos(a) * rr;
         double z = at.z + Math.sin(a) * rr;
         Vec3 toward = new Vec3(at.x - x, 0.55, at.z - z).normalize().scale(0.28 + (double)progress * 0.5);
         level.sendParticles(ParticleTypes.END_ROD, x, at.y + 0.1, z, 0, toward.x, toward.y, toward.z, 1.0);
      }
   }

   public static void column(ServerLevel level, Vec3 at, double radius, boolean perfect) {
      play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 4.0F, 0.75F);
      play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 0.7F);
      play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 3.0F, 1.4F);
      play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 2.4F, 0.9F);
      if (perfect) {
         play(level, at, "minecraft:block.note_block.chime", SoundEvents.NOTE_BLOCK_CHIME.value(), 2.0F, 2.0F);
         play(level, at, "minecraft:entity.wither.spawn", SoundEvents.WITHER_SPAWN, 1.2F, 1.6F);
      }

      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 1.5, at.z, 6, 0.4, 1.0, 0.4, 0.0);
      level.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y + 1.2, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      ring(level, at, radius, (int)Math.max(60.0, radius * 5.0), 0.9);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 45, at, (float)(radius * 7.0)));
   }

   public static void columnStep(ServerLevel level, Vec3 at, double radius, int tick, boolean perfect) {
      if (tick % 3 == 0) {
         play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 1.6F, 0.5F + (float)tick * 0.02F);
      }

      if (tick % 2 == 0) {
         double travelled = radius * (0.6 + (double)tick * 0.08);
         ring(level, at, travelled, (int)Math.max(24.0, travelled * 2.5), 0.05);
      }

      if (tick % 2 == 0) {
         level.sendParticles(perfect ? ParticleTypes.END_ROD : ParticleTypes.ELECTRIC_SPARK, at.x, at.y + 3.0, at.z, 25, 0.6, 3.0, 0.6, 0.5);
      }
   }

   public static void collapses(ServerLevel level, Vec3 at, double radius) {
      play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 2.2F, 0.7F);
      play(level, at, "minecraft:block.respawn_anchor.deplete", SoundEvents.BEACON_DEACTIVATE, 1.8F, 0.6F);
      int points = (int)Math.max(16.0, radius * 2.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(ParticleTypes.SMOKE, at.x + Math.cos(a) * radius, at.y + 0.2, at.z + Math.sin(a) * radius, 1, 0.1, 0.02, 0.1, 0.01);
      }
   }

   public static void struck(ServerLevel level, Entity target, boolean perfect) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 24, 0.3, 0.5, 0.3, 0.4);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 18, 0.3, 0.4, 0.3, 0.25);
      level.sendParticles(ParticleTypes.SWEEP_ATTACK, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      if (perfect) {
         level.sendParticles(ParticleTypes.FLASH, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      }

      play(level, at, "minecraft:entity.player.attack.crit", SoundEvents.PLAYER_ATTACK_CRIT, 1.3F, 0.8F);
   }

   private static void ring(ServerLevel level, Vec3 at, double radius, int points, double lift) {
      int capped = Mth.clamp(points, 8, 160);

      for (int i = 0; i < capped; i++) {
         double a = (Math.PI * 2) * (double)i / (double)capped;
         level.sendParticles(
            ParticleTypes.END_ROD, at.x + Math.cos(a) * radius, at.y + 0.12, at.z + Math.sin(a) * radius, 1, 0.0, lift, 0.0, lift > 0.3 ? 0.12 : 0.0
         );
      }
   }
}
