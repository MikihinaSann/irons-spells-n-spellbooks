package net.hazen.hazennstuff.Registries.Effects;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EnigmaticWitheringEffect extends MagicMobEffect implements ISyncedMobEffect {
   public EnigmaticWitheringEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      return super.applyEffectTick(livingEntity, amplifier);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
