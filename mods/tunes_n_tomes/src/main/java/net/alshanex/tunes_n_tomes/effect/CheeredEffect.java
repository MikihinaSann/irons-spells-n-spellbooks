package net.alshanex.tunes_n_tomes.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.tunes_n_tomes.entity.green_effect.GreenEffectEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CheeredEffect extends MagicMobEffect {
   public CheeredEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
      if (!pLivingEntity.level().isClientSide) {
         GreenEffectEntity effect = new GreenEffectEntity(pLivingEntity.level());
         effect.moveTo(pLivingEntity.position());
         pLivingEntity.level().addFreshEntity(effect);
      }

      super.onEffectAdded(pLivingEntity, pAmplifier);
   }
}
