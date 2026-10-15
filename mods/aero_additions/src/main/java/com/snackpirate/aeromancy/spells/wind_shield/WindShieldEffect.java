package com.snackpirate.aeromancy.spells.wind_shield;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WindShieldEffect extends MagicMobEffect implements ISyncedMobEffect {
   public WindShieldEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectRemoved(pLivingEntity, pAmplifier);
   }

   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
      super.onEffectAdded(pLivingEntity, pAmplifier);
   }
}
