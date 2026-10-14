package com.gametechbc.traveloptics.effects.reversal;

import com.gametechbc.traveloptics.init.TOEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ReversalEffectInstance extends MobEffectInstance {
   private float damageMultiplier;

   public ReversalEffectInstance(
      DeferredHolder<MobEffect, MobEffect> effect,
      int duration,
      int amplifier,
      boolean ambient,
      boolean showParticles,
      boolean showIcon,
      float damageMultiplier
   ) {
      super(TOEffects.REVERSAL, duration, amplifier, ambient, showParticles, showIcon);
      this.damageMultiplier = damageMultiplier;
   }

   public float getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public void setDamageMultiplier(float damageMultiplier) {
      this.damageMultiplier = damageMultiplier;
   }
}
