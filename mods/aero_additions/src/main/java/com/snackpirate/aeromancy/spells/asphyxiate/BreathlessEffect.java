package com.snackpirate.aeromancy.spells.asphyxiate;

import com.snackpirate.aeromancy.data.AADamageTypes;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BreathlessEffect extends MagicMobEffect implements ISyncedMobEffect {
   public BreathlessEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
      livingEntity.hurt(DamageSources.get(livingEntity.level(), AADamageTypes.ASPHYXIATION), 1.0F + 0.2F * amplifier);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % (6 - amplifier) == 0;
   }

   public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
      super.onEffectAdded(livingEntity, amplifier);
   }

   public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectRemoved(pLivingEntity, pAmplifier);
   }
}
