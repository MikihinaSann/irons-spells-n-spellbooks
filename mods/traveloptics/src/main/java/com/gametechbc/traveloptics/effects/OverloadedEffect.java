package com.gametechbc.traveloptics.effects;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

public class OverloadedEffect extends MobEffect {
   public OverloadedEffect() {
      super(MobEffectCategory.HARMFUL, 0);
   }

   public boolean applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
