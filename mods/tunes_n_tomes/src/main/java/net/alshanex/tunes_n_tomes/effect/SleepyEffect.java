package net.alshanex.tunes_n_tomes.effect;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallEffectEntity;
import net.alshanex.tunes_n_tomes.registry.TParticleRegistry;
import net.alshanex.tunes_n_tomes.util.CylinderParticleManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SleepyEffect extends MagicMobEffect implements ISyncedMobEffect {
   public SleepyEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      if (!pLivingEntity.level().isClientSide) {
         CylinderParticleManager.spawnParticlesAtBlockPos(
            pLivingEntity.level(),
            pLivingEntity.position(),
            1,
            (ParticleOptions)TParticleRegistry.SLEEP_PARTICLE.get(),
            CylinderParticleManager.ParticleDirection.UPWARD,
            0.1,
            0.0,
            pLivingEntity.getBbHeight()
         );
      }

      return super.applyEffectTick(pLivingEntity, pAmplifier);
   }

   public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
      if (!pLivingEntity.level().isClientSide) {
         BlueBallEffectEntity effect = new BlueBallEffectEntity(pLivingEntity.level());
         effect.moveTo(pLivingEntity.position());
         pLivingEntity.level().addFreshEntity(effect);
      }

      super.onEffectAdded(pLivingEntity, pAmplifier);
   }
}
