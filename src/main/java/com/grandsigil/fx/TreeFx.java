package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class TreeFx {
   private TreeFx() {
   }

   public static void erupts(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.gravel.break", SoundEvents.GENERIC_EXPLODE, 5.0F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.ravager.roar", SoundEvents.ENDER_DRAGON_GROWL, 3.5F, 0.5F);
      GsFx.play(level, at, "minecraft:block.wood.break", SoundEvents.GENERIC_EXPLODE, 4.0F, 0.4F);
      GsFx.play(level, at, "minecraft:block.azalea_leaves.place", SoundEvents.BEACON_ACTIVATE, 3.0F, 0.7F);
      int points = (int)Math.max(40.0, radius * 3.0);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         double rr = radius * (0.3 + level.random.nextDouble() * 0.7);
         level.sendParticles(ParticleTypes.COMPOSTER, at.x + Math.cos(a) * rr, at.y + 0.3, at.z + Math.sin(a) * rr, 3, 0.3, 0.4, 0.3, 0.15);
      }

      CameraShakeManager.addCameraShake(new CameraShakeData(level, 40, at, (float)(radius * 1.6)));
   }

   public static void creaking(ServerLevel level, Vec3 at, double radius, int generation) {
      GsFx.play(level, at, "minecraft:block.wooden_door.open", SoundEvents.BEACON_AMBIENT, 2.2F, 0.5F - (float)generation * 0.05F);
      GsFx.play(level, at, "minecraft:block.azalea_leaves.break", SoundEvents.FIRE_AMBIENT, 1.6F, 0.7F);
   }

   public static void fed(ServerLevel level, Vec3 at, double radius, int generation) {
      float pitch = (float)Math.pow(2.0, (double)(generation - 1) / 6.0);
      GsFx.play(level, at, "minecraft:block.note_block.bass", SoundEvents.NOTE_BLOCK_CHIME.value(), 2.4F, pitch);
      GsFx.play(level, at, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 1.8F, pitch);
      GsFx.play(level, at, "minecraft:block.azalea_leaves.place", SoundEvents.BEACON_ACTIVATE, 2.0F, 0.8F);
      level.sendParticles(IronParticles.or("irons_spellbooks:firefly", ParticleTypes.HAPPY_VILLAGER), at.x, at.y + 2.0, at.z, 60, 4.0, 3.0, 4.0, 0.08);
   }

   public static void mended(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(IronParticles.or("irons_spellbooks:cleanse", ParticleTypes.HAPPY_VILLAGER), at.x, at.y, at.z, 5, 0.35, 0.5, 0.35, 0.02);
   }

   public static void bled(ServerLevel level, Entity target) {
      Vec3 at = target.getBoundingBox().getCenter();
      level.sendParticles(IronParticles.or("irons_spellbooks:acid", ParticleTypes.COMPOSTER), at.x, at.y - 0.4, at.z, 6, 0.3, 0.3, 0.3, 0.04);
   }

   public static void blooms(ServerLevel level, Vec3 at, double radius, int generation) {
      GsFx.play(level, at, "minecraft:entity.evoker.cast_spell", SoundEvents.EVOKER_PREPARE_SUMMON, 4.0F, 0.7F);
      GsFx.play(level, at, "minecraft:block.azalea_leaves.break", SoundEvents.FIRE_AMBIENT, 5.0F, 0.6F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 4.0F, 0.8F);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + 12.0, at.z, 3, 2.0, 2.0, 2.0, 0.0);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 35, at, (float)(radius * 2.0)));
   }

   public static void spores(ServerLevel level, Vec3 at, double radius, int tick, int generation) {
      int count = 10 + generation * 5;

      for (int i = 0; i < count; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double rr = Math.sqrt(level.random.nextDouble()) * radius;
         level.sendParticles(
            IronParticles.or("irons_spellbooks:firefly", ParticleTypes.SPORE_BLOSSOM_AIR),
            at.x + Math.cos(a) * rr,
            at.y + 6.0 + level.random.nextDouble() * 14.0,
            at.z + Math.sin(a) * rr,
            1,
            0.4,
            0.4,
            0.4,
            0.02
         );
      }

      if (tick % 5 == 0) {
         double travelled = radius * (0.3 + (double)tick * 0.06);
         int points = (int)Mth.clamp(travelled * 2.0, 24.0, 120.0);

         for (int i = 0; i < points; i++) {
            double a = (Math.PI * 2) * (double)i / (double)points;
            level.sendParticles(
               ParticleTypes.HAPPY_VILLAGER, at.x + Math.cos(a) * travelled, at.y + 0.4, at.z + Math.sin(a) * travelled, 1, 0.1, 0.2, 0.1, 0.03
            );
         }
      }
   }
}
