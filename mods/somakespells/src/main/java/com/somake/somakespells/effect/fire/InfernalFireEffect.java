package com.somake.somakespells.effect.fire;

import com.somake.somakespells.registries.ModParticles;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class InfernalFireEffect extends MagicMobEffect implements ISyncedMobEffect {
   private static final String INFERNAL_SURGE_PARTICLE_ID = "born_in_chaos_v1:infernal_surge";

   public InfernalFireEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity.level().isClientSide) {
         return true;
      } else {
         ServerLevel level = (ServerLevel)entity.level();
         double x = entity.getX();
         double y = entity.getY();
         double z = entity.getZ();
         level.sendParticles((SimpleParticleType)ModParticles.INFERNAL_FIRE_FLAME.get(), x, y + 1.0, z, 1, 0.3, 0.3, 0.3, 0.1);
         if (entity.getPersistentData().getDouble("infernal_fire_timer") == 0.0) {
            entity.getPersistentData().putDouble("infernal_fire_timer", 40.0);
         } else {
            entity.getPersistentData().putDouble("infernal_fire_timer", entity.getPersistentData().getDouble("infernal_fire_timer") - 1.0);
         }

         if (entity.getPersistentData().getDouble("infernal_fire_timer") != 0.0) {
            return true;
         } else {
            float health = entity.getHealth();
            float damage;
            if (health >= 30.0F) {
               damage = 5.0F;
            } else if (health > 6.0F) {
               damage = 3.0F;
            } else if (health > 3.0F) {
               damage = 2.0F;
            } else {
               damage = 1.0F;
            }

            entity.hurt(entity.damageSources().inFire(), damage);
            level.sendParticles(getInfernalSurgeParticle(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
            SoundEvent hurtSound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire"));
            if (hurtSound != null) {
               level.playSound(null, BlockPos.containing(x, y, z), hurtSound, SoundSource.NEUTRAL, 0.9F, 0.8F);
            }

            return true;
         }
      }
   }

   private static ParticleOptions getInfernalSurgeParticle() {
      ParticleType<?> type = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse("born_in_chaos_v1:infernal_surge"));
      return type instanceof ParticleOptions options ? options : (ParticleOptions)ModParticles.INFERNAL_FIRE_FLAME_JET.get();
   }
}
