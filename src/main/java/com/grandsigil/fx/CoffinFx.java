package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class CoffinFx {
   private CoffinFx() {
   }

   public static void closes(ServerLevel level, Vec3 at, double half) {
      GsFx.play(level, at, "minecraft:block.portal.trigger", SoundEvents.PORTAL_TRIGGER, 4.0F, 0.35F);
      GsFx.play(level, at, "minecraft:entity.wither.spawn", SoundEvents.WITHER_SPAWN, 2.6F, 0.45F);
      GsFx.play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 3.0F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.warden.sonic_boom", SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 0.5F);
      level.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y + half * 0.6, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      square(level, at, half, 0.8);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 40, at, (float)(half * 8.0)));
   }

   public static void rising(ServerLevel level, Vec3 at, double half, float progress) {
      GsFx.play(level, at, "minecraft:block.piston.extend", SoundEvents.PISTON_EXTEND, 2.6F, 0.32F + progress * 0.2F);
      GsFx.play(level, at, "minecraft:block.stone.place", SoundEvents.STONE_PLACE, 3.0F, 0.3F + progress * 0.25F);
      GsFx.play(level, at, "minecraft:block.portal.ambient", SoundEvents.PORTAL_AMBIENT, 1.6F, 0.4F + progress * 0.5F);
      int count = 6 + Mth.floor(progress * 10.0F);

      for (int i = 0; i < count; i++) {
         double a = level.random.nextDouble() * Math.PI * 2.0;
         double edge = half * 1.02;
         double x = at.x + (double)Mth.clamp((float)(Math.cos(a) * half * 1.6), (float)(-edge), (float)edge);
         double z = at.z + (double)Mth.clamp((float)(Math.sin(a) * half * 1.6), (float)(-edge), (float)edge);
         level.sendParticles(ParticleTypes.PORTAL, x, at.y + 0.2, z, 0, 0.0, 0.6 + level.random.nextDouble() * 0.8, 0.0, 1.0);
      }
   }

   public static void lidded(ServerLevel level, Vec3 at, double half) {
      GsFx.play(level, at, "minecraft:block.anvil.land", SoundEvents.ANVIL_LAND, 3.2F, 0.3F);
      GsFx.play(level, at, "minecraft:block.enchantment_table.use", SoundEvents.ENCHANTMENT_TABLE_USE, 2.4F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.ender_dragon.growl", SoundEvents.ENDER_DRAGON_GROWL, 2.2F, 0.35F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, at, (float)(half * 6.0)));
   }

   public static void driven(ServerLevel level, Vec3 at, double half, int index) {
      float fall = (float)index / 24.0F;
      GsFx.play(level, at, "minecraft:entity.player.attack.crit", SoundEvents.PLAYER_ATTACK_CRIT, 1.6F, 1.25F - fall * 0.55F);
      GsFx.play(level, at, "minecraft:block.stone.break", SoundEvents.STONE_BREAK, 2.2F, 0.55F - fall * 0.15F);
      if (index % 3 == 0) {
         GsFx.play(level, at, "minecraft:entity.wither.shoot", SoundEvents.WITHER_SHOOT, 1.4F, 0.5F - fall * 0.15F);
      }

      level.sendParticles(ParticleTypes.PORTAL, at.x, at.y + half * 0.9, at.z, 14, half * 0.4, half * 0.5, half * 0.4, 0.7);
   }

   public static void breaks(ServerLevel level, Vec3 at, double half) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 6.0F, 0.32F);
      GsFx.play(level, at, "minecraft:block.end_portal_frame.fill", SoundEvents.END_PORTAL_FRAME_FILL, 4.0F, 0.4F);
      GsFx.play(level, at, "minecraft:entity.wither.break_block", SoundEvents.WITHER_BREAK_BLOCK, 3.4F, 0.35F);
      GsFx.play(level, at, "minecraft:block.beacon.deactivate", SoundEvents.BEACON_DEACTIVATE, 3.0F, 0.6F);
      level.sendParticles(ParticleTypes.FLASH, at.x, at.y + half, at.z, 3, 0.5, 1.0, 0.5, 0.0);
      level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 0.5, at.z, 50, half * 0.7, half * 0.6, half * 0.7, 0.06);
      level.sendParticles(ParticleTypes.PORTAL, at.x, at.y + half, at.z, 80, half * 0.6, half * 0.9, half * 0.6, 1.1);
      square(level, at, half * 1.6, 0.25);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 36, at, (float)(half * 8.0)));
   }

   private static void square(ServerLevel level, Vec3 at, double half, double lift) {
      int perSide = Mth.clamp((int)(half * 2.2), 4, 40);

      for (int side = 0; side < 4; side++) {
         for (int i = 0; i < perSide; i++) {
            double t = -half + 2.0 * half * (double)i / ((double)perSide - 1.0);

            double x = switch (side) {
               case 0 -> at.x + t;
               case 1 -> at.x + t;
               case 2 -> at.x - half;
               default -> at.x + half;
            };

            double z = switch (side) {
               case 0 -> at.z - half;
               case 1 -> at.z + half;
               default -> at.z + t;
            };
            level.sendParticles(ParticleTypes.PORTAL, x, at.y + 0.15, z, 1, 0.0, lift, 0.0, lift > 0.5 ? 0.6 : 0.0);
         }
      }
   }
}
