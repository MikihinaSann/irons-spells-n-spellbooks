package net.alshanex.alshanex_familiars.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.familiarslib.registry.FParticleRegistry;
import net.alshanex.familiarslib.util.CylinderParticleManager;
import net.alshanex.familiarslib.util.CylinderParticleManager.ParticleDirection;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SleepyEffect extends MagicMobEffect {
   public SleepyEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      if (!pLivingEntity.level.isClientSide) {
         CylinderParticleManager.spawnParticlesAtBlockPos(
            pLivingEntity.level,
            pLivingEntity.position(),
            1,
            (ParticleOptions)FParticleRegistry.SLEEP_PARTICLE.get(),
            ParticleDirection.UPWARD,
            0.1,
            0.0,
            pLivingEntity.getBbHeight()
         );
      }

      return super.applyEffectTick(pLivingEntity, pAmplifier);
   }
}
