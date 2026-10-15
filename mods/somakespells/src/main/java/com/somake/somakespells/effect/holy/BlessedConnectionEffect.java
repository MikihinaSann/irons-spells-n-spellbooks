package com.somake.somakespells.effect.holy;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BlessedConnectionEffect extends MagicMobEffect implements ISyncedMobEffect {
   public BlessedConnectionEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
