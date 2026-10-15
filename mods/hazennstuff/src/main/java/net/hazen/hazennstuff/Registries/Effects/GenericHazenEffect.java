package net.hazen.hazennstuff.Registries.Effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GenericHazenEffect extends MobEffect {
   public GenericHazenEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      return super.applyEffectTick(livingEntity, amplifier);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
