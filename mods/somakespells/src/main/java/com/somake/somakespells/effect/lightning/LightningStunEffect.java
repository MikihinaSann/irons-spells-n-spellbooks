package com.somake.somakespells.effect.lightning;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class LightningStunEffect extends MagicMobEffect {
   public LightningStunEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      livingEntity.setDeltaMovement(0.0, 0.0, 0.0);
      livingEntity.setJumping(false);
      livingEntity.setSprinting(false);
      if (livingEntity instanceof Mob mob) {
         mob.getNavigation().stop();
         mob.setTarget(null);
      }

      return true;
   }
}
