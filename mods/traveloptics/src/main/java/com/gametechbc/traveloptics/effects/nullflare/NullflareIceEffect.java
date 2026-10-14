package com.gametechbc.traveloptics.effects.nullflare;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class NullflareIceEffect extends MobEffect {
   private LivingEntity cachedEntity;

   public NullflareIceEffect() {
      super(MobEffectCategory.HARMFUL, 65535);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide() && entity.canFreeze()) {
         int freezeIncrease = (amplifier + 1) * 2;
         entity.setTicksFrozen(entity.getTicksFrozen() + freezeIncrease);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
