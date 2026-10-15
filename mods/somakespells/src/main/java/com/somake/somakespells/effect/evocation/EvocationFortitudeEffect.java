package com.somake.somakespells.effect.evocation;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EvocationFortitudeEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float EVOCATION_SPELL_POWER_PER_LEVEL = 0.05F;

   public EvocationFortitudeEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
