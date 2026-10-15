package com.somake.somakespells.effect.spirit;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SpiritEmpowermentEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float SPIRIT_SPELL_POWER_PER_LEVEL = 0.05F;

   public SpiritEmpowermentEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
