package com.somake.somakespells.effect.eldritch;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EldritchGambitEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float ELDRITCH_SPELL_POWER_PER_LEVEL = 0.05F;

   public EldritchGambitEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
