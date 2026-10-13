package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ReckoningFx {
   private ReckoningFx() {
   }

   public static void begins(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 1.8F, 1.5F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 1.6F, 1.4F);
      GsFx.play(level, at, "minecraft:entity.illusioner.prepare_mirror", SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.2F, 1.2F);
   }

   public static void gathering(ServerLevel level, Vec3 at, float fill, int tick) {
      Vec3 hands = at.add(0.0, 1.35, 0.0);
      if (tick % 4 == 0) {
         GsFx.play(level, hands, "minecraft:block.note_block.bell", SoundEvents.NOTE_BLOCK_BELL.value(), 0.8F, 0.6F + fill * 1.3F);
      }

      if (tick % 8 == 0) {
         GsFx.play(level, hands, "minecraft:block.conduit.ambient", SoundEvents.CONDUIT_AMBIENT, 0.9F, 0.7F + fill * 0.8F);
      }

      if (tick % 20 == 0) {
         GsFx.play(level, hands, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 1.1F, 0.6F + fill * 1.2F);
      }

      if (tick % 3 == 0) {
         int count = 2 + Mth.floor(fill * 6.0F);

         for (int i = 0; i < count; i++) {
            double a = level.random.nextDouble() * Math.PI * 2.0;
            double rr = 2.0 + level.random.nextDouble() * (2.5 + (double)fill * 4.0);
            double x = hands.x + Math.cos(a) * rr;
            double y = hands.y + (level.random.nextDouble() - 0.5) * 2.5;
            double z = hands.z + Math.sin(a) * rr;
            Vec3 toward = new Vec3(hands.x - x, hands.y - y, hands.z - z).normalize().scale(0.2 + (double)fill * 0.55);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 0, toward.x, toward.y, toward.z, 1.0);
         }

         if (fill > 0.72F && tick % 6 == 0) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, hands.x, hands.y, hands.z, 4, 0.25, 0.25, 0.25, 0.03);
         }
      }
   }

   public static void lost(ServerLevel level, Vec3 at) {
      Vec3 hands = at.add(0.0, 1.35, 0.0);
      GsFx.play(level, hands, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 1.6F, 1.3F);
      GsFx.play(level, hands, "minecraft:block.fire.extinguish", SoundEvents.FIRE_EXTINGUISH, 1.2F, 0.7F);
      level.sendParticles(ParticleTypes.SMOKE, hands.x, hands.y, hands.z, 14, 0.2, 0.2, 0.2, 0.01);
   }

   public static void released(ServerLevel level, Vec3 target) {
      GsFx.play(level, target, "minecraft:item.totem.use", SoundEvents.TOTEM_USE, 3.0F, 0.75F);
      GsFx.play(level, target, "minecraft:entity.evoker.cast_spell", SoundEvents.EVOKER_CAST_SPELL, 2.4F, 0.6F);
      GsFx.play(level, target, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 2.0F, 2.0F);
   }

   public static void lands(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.lightning_bolt.thunder", SoundEvents.LIGHTNING_BOLT_THUNDER, 8.0F, 0.6F);
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 6.0F, 0.55F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 7.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.conduit.activate", SoundEvents.CONDUIT_ACTIVATE, 5.0F, 0.9F);
      GsFx.play(level, at, "minecraft:item.trident.thunder", SoundEvents.TRIDENT_THUNDER, 4.0F, 1.1F);
      GsFx.play(level, at, "minecraft:block.note_block.chime", SoundEvents.NOTE_BLOCK_CHIME.value(), 3.0F, 2.0F);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 2.0, at.z, 8, 0.6, 1.5, 0.6, 0.0);
      level.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y + 1.5, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      ring(level, at, radius * 0.9, 0.9);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 50, at, (float)(radius * 7.0)));
   }

   public static void standing(ServerLevel level, Vec3 at, double radius, float progress) {
      GsFx.play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 2.6F, 1.5F - progress * 0.35F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 1.6F, 1.6F - progress * 0.4F);
      ring(level, at, radius * (1.0 + (double)progress * 2.2), 0.05);
   }

   public static void closes(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 2.6F, 1.2F);
      GsFx.play(level, at, "minecraft:block.conduit.deactivate", SoundEvents.CONDUIT_DEACTIVATE, 2.0F, 1.0F);
   }

   public static void judged(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 30, 0.35, 0.6, 0.35, 0.5);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 16, 0.3, 0.4, 0.3, 0.2);
      GsFx.play(level, at, "minecraft:entity.player.levelup", SoundEvents.PLAYER_LEVELUP, 1.2F, 0.7F);
   }

   private static void ring(ServerLevel level, Vec3 at, double radius, double lift) {
      int points = Mth.clamp((int)(radius * 2.5), 8, 160);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(
            ParticleTypes.END_ROD, at.x + Math.cos(a) * radius, at.y + 0.12, at.z + Math.sin(a) * radius, 1, 0.0, lift, 0.0, lift > 0.3 ? 0.12 : 0.0
         );
      }
   }
}
