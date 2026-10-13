package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public final class CalderaFx {
   private CalderaFx() {
   }

   public static void opens(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 4.0F, 0.3F);
      GsFx.play(level, at, "minecraft:block.deepslate.break", SoundEvents.DEEPSLATE_BREAK, 4.0F, 0.35F);
      GsFx.play(level, at, "minecraft:block.lava.pop", SoundEvents.LAVA_POP, 3.0F, 0.4F);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 50, at, (float)radius));
      level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 1.0, at.z, 140, radius * 0.4, 2.0, radius * 0.4, 0.1);
   }

   public static void rumbles(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.lava.ambient", SoundEvents.LAVA_AMBIENT, 2.4F, 0.5F);
      GsFx.play(level, at, "minecraft:ambient.nether_wastes.mood", SoundEvents.AMBIENT_NETHER_WASTES_MOOD.value(), 1.6F, 0.6F);
      int points = (int)Math.max(20.0, radius);

      for (int i = 0; i < points; i++) {
         double a = (Math.PI * 2) * (double)i / (double)points;
         level.sendParticles(ParticleTypes.SMOKE, at.x + Math.cos(a) * radius, at.y + 0.4, at.z + Math.sin(a) * radius, 1, 0.2, 0.1, 0.2, 0.01);
      }
   }

   public static void closes(ServerLevel level, Vec3 at, double radius) {
      GsFx.play(level, at, "minecraft:block.lava.extinguish", SoundEvents.LAVA_EXTINGUISH, 3.5F, 0.4F);
      GsFx.play(level, at, "minecraft:block.basalt.break", SoundEvents.BASALT_BREAK, 3.0F, 0.5F);
      level.sendParticles(ParticleTypes.CLOUD, at.x, at.y + 1.0, at.z, 120, radius * 0.45, 1.5, radius * 0.45, 0.05);
   }
}
