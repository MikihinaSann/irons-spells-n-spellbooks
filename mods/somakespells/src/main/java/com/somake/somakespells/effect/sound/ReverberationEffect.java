package com.somake.somakespells.effect.sound;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ReverberationEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float SOUND_SPELL_POWER_PER_LEVEL = 0.05F;

   public ReverberationEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
