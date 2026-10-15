package com.somake.somakespells.effect.symmetry;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SymmetryEmpowermentEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float SYMMETRY_SPELL_POWER_PER_LEVEL = 0.05F;

   public SymmetryEmpowermentEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
