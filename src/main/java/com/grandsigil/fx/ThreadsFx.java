package com.grandsigil.fx;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ThreadsFx {
   private static final DustParticleOptions BLOOD = new DustParticleOptions(new Vector3f(0.6F, 0.02F, 0.05F), 2.0F);

   private ThreadsFx() {
   }

   public static void rises(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:block.sculk_catalyst.bloom", SoundEvents.SCULK_CATALYST_BLOOM, 4.0F, 0.5F);
      GsFx.play(level, at, "minecraft:block.bubble_column.upwards_ambient", SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 4.0F, 0.5F);
      level.sendParticles(BLOOD, at.x, at.y + 0.5, at.z, 120, 6.0, 0.3, 6.0, 0.05);
   }

   public static void beats(ServerLevel level, Vec3 heart, int bound) {
      float volume = 3.0F + Math.min(4.0F, (float)bound * 0.25F);
      GsFx.play(level, heart, "minecraft:entity.warden.heartbeat", SoundEvents.WARDEN_HEARTBEAT, volume, 0.6F);
   }

   public static void binds(ServerLevel level, Vec3 heart, int count) {
      GsFx.play(level, heart, "minecraft:block.sculk_shrieker.shriek", SoundEvents.SCULK_SHRIEKER_SHRIEK, 4.0F, 0.8F);
      GsFx.play(level, heart, "minecraft:entity.player.attack.sweep", SoundEvents.PLAYER_ATTACK_SWEEP, 3.0F, 0.6F);
   }

   public static void snaps(ServerLevel level, Vec3 heart, List<LivingEntity> targets) {
      GsFx.play(level, heart, "minecraft:block.chain.break", SoundEvents.CHAIN_BREAK, 5.0F, 0.5F);
      GsFx.play(level, heart, "minecraft:entity.player.attack.crit", SoundEvents.PLAYER_ATTACK_CRIT, 5.0F, 0.6F);

      for (LivingEntity e : targets) {
         Vec3 c = e.getBoundingBox().getCenter();
         level.sendParticles(BLOOD, c.x, c.y, c.z, 30, 0.5, 0.6, 0.5, 0.2);
         level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, c.x, c.y, c.z, 6, 0.4, 0.4, 0.4, 0.2);
      }

      CameraShakeManager.addCameraShake(new CameraShakeData(level, 14, heart, 50.0F));
   }

   public static void takenIn(ServerLevel level, Vec3 at) {
      GsFx.play(level, at, "minecraft:item.totem.use", SoundEvents.TOTEM_USE, 4.0F, 0.6F);
      GsFx.play(level, at, "minecraft:entity.generic.explode", SoundEvents.GENERIC_EXPLODE, 4.0F, 0.7F);
      GsFx.play(level, at, "minecraft:entity.warden.heartbeat", SoundEvents.WARDEN_HEARTBEAT, 6.0F, 0.5F);
      level.sendParticles(BLOOD, at.x, at.y + 1.0, at.z, 300, 4.0, 1.5, 4.0, 0.4);
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, at, 30.0F));
   }
}
