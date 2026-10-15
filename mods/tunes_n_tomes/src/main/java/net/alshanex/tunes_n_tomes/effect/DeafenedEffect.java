package net.alshanex.tunes_n_tomes.effect;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallEffectEntity;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DeafenedEffect extends MagicMobEffect implements ISyncedMobEffect {
   public DeafenedEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      return true;
   }

   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
      if (!pLivingEntity.level().isClientSide) {
         RedBallEffectEntity effect = new RedBallEffectEntity(pLivingEntity.level());
         effect.moveTo(pLivingEntity.position());
         pLivingEntity.level().addFreshEntity(effect);
         if (pLivingEntity.hasEffect(TEffectRegistry.SLEEPY)) {
            pLivingEntity.removeEffect(TEffectRegistry.SLEEPY);
         }
      }

      super.onEffectAdded(pLivingEntity, pAmplifier);
   }
}
