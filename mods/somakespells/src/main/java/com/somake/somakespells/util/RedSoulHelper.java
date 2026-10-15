package com.somake.somakespells.util;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.entity.RedEchoEntity;
import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;

public final class RedSoulHelper {
   private RedSoulHelper() {
   }

   public static boolean isActive(LivingEntity entity) {
      return LegendaryMonstersCompat.isLoaded()
         && (entity instanceof RedEchoEntity || entity != null && ModMobEffects.RED_SOUL != null && entity.hasEffect(ModMobEffects.RED_SOUL));
   }

   public static String particle(boolean redSoul, String normalPath) {
      return redSoul ? normalPath + "_red" : normalPath;
   }
}
