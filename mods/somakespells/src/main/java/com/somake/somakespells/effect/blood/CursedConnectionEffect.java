package com.somake.somakespells.effect.blood;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CursedConnectionEffect extends MagicMobEffect implements ISyncedMobEffect {
   public CursedConnectionEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
