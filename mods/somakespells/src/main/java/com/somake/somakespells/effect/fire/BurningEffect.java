package com.somake.somakespells.effect.fire;

import com.somake.somakespells.registries.ModAttributes;
import com.somake.somakespells.registries.ModParticles;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BurningEffect extends MagicMobEffect implements ISyncedMobEffect {
   public BurningEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         ModAttributes.AQUA_MAGIC_RESIST, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.burning.aqua_resist"), 0.05, Operation.ADD_VALUE
      );
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
         level.sendParticles((SimpleParticleType)ModParticles.FLAME_ANIM_FIRE.get(), x, y + 1.0, z, 1, 0.3, 0.3, 0.3, 0.1);
         if (entity.getPersistentData().getDouble("burning_effect_timer") == 0.0) {
            entity.getPersistentData().putDouble("burning_effect_timer", 40.0);
         } else {
            entity.getPersistentData().putDouble("burning_effect_timer", entity.getPersistentData().getDouble("burning_effect_timer") - 1.0);
         }

         if (entity.getPersistentData().getDouble("burning_effect_timer") != 0.0) {
            return true;
         } else {
            if (!entity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
               float health = entity.getHealth();
               float dmg = 0.0F;
               if (health >= 30.0F) {
                  dmg = 5.0F;
               } else if (health > 6.0F) {
                  dmg = 3.0F;
               } else if (health > 3.0F) {
                  dmg = 2.0F;
               } else {
                  dmg = 1.0F;
               }

               entity.hurt(entity.damageSources().inFire(), dmg);
               level.sendParticles((SimpleParticleType)ModParticles.FLAME_SURGE.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
               SoundEvent hurtSound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_on_fire"));
               if (hurtSound != null) {
                  level.playSound(null, BlockPos.containing(x, y, z), hurtSound, SoundSource.NEUTRAL, 0.9F, 0.8F);
               }
            }

            return true;
         }
      }
   }
}
