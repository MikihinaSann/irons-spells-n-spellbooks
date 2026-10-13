package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class BloomFx {
   private BloomFx() {
   }

   public static void sprouts(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.rooted_dirt.break", SoundEvents.ROOTED_DIRT_BREAK, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.big_dripleaf.tilt_up", SoundEvents.BIG_DRIPLEAF_TILT_UP, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.resonate", SoundEvents.AMETHYST_BLOCK_RESONATE, 4.0F, 0.6F);
      level.sendParticles(ParticleTypes.HAPPY_VILLAGER, at.x, at.y + 0.5, at.z, 120, 8.0, 0.3, 8.0, 0.1);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, at, 30.0F));
   }

   public static void rises(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.azalea.place", SoundEvents.AZALEA_PLACE, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.beacon.activate", SoundEvents.BEACON_ACTIVATE, 4.0F, 0.7F);
   }

   public static void opens(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.bell.resonate", SoundEvents.BELL_RESONATE, 5.0F, 1.2F);
      GsFx.play(level, at, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 5.0F, 0.8F);
      GsFx.play(level, at, "minecraft:block.beacon.power_select", SoundEvents.BEACON_POWER_SELECT, 5.0F, 1.3F);
      level.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 160, 8.0, 3.0, 8.0, 0.15);
   }

   public static void hums(ServerLevel level, Vec3 at, int age) {
      GsFx.play(level, at, "minecraft:block.beacon.ambient", SoundEvents.BEACON_AMBIENT, 4.0F, 0.7F + (float)(age % 60) * 0.004F);
   }

   public static void wave(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.amethyst_block.chime", SoundEvents.AMETHYST_BLOCK_CHIME, 4.0F, 1.2F);
      GsFx.play(level, at, "minecraft:block.azalea_leaves.break", SoundEvents.AZALEA_LEAVES_BREAK, 4.0F, 0.6F);
   }

   public static void bursts(ServerLevel level, Vec3 top, Vec3 root) {
      GsFx.play(level, top, "minecraft:block.amethyst_cluster.break", SoundEvents.AMETHYST_CLUSTER_BREAK, 6.0F, 0.6F);
      GsFx.play(level, top, "minecraft:block.bell.use", SoundEvents.BELL_BLOCK, 6.0F, 0.8F);
      GsFx.play(level, root, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 4.0F, 1.3F);
      GsFx.play(level, top, "minecraft:item.totem.use", SoundEvents.TOTEM_USE, 3.0F, 1.2F);
      level.sendParticles(ParticleTypes.HAPPY_VILLAGER, top.x, top.y, top.z, 300, 14.0, 6.0, 14.0, 0.5);
      level.sendParticles(ParticleTypes.END_ROD, top.x, top.y, top.z, 260, 10.0, 5.0, 10.0, 0.4);
      level.sendParticles(ParticleTypes.CHERRY_LEAVES, top.x, top.y, top.z, 400, 16.0, 8.0, 16.0, 0.1);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 24, root, 60.0F));
   }
}
