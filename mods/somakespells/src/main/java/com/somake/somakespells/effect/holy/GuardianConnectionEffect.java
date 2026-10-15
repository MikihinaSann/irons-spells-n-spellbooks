package com.somake.somakespells.effect.holy;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GuardianConnectionEffect extends MagicMobEffect implements ISyncedMobEffect {
   public GuardianConnectionEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
