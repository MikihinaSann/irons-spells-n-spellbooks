package com.somake.somakespells.effect.fire;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DamnedDemomansEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float DAMAGE_PER_LEVEL = 5.0F;
   public static final float HEALTH_THRESHOLD = 0.3F;

   public DamnedDemomansEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
   }
}
