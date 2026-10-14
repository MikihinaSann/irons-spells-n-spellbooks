package com.gametechbc.traveloptics.api.utils;

import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SummonCheckHelper {
   public SummonCheckHelper() {
      super();
   }

   public static boolean hasActiveSummons(Player player, double range, Set<Class<? extends IMagicSummon>> summonRegistry) {
      if (player.level() instanceof ServerLevel level) {
         List<LivingEntity> summons = level.getEntitiesOfClass(
            LivingEntity.class,
            player.getBoundingBox().inflate(range),
            entity -> isRegisteredSummon(entity, summonRegistry)
               && entity instanceof IMagicSummon magicSummon
               && magicSummon.getSummoner() != null
               && magicSummon.getSummoner().getUUID().equals(player.getUUID())
         );
         return !summons.isEmpty();
      } else {
         return false;
      }
   }

   private static boolean isRegisteredSummon(LivingEntity entity, Set<Class<? extends IMagicSummon>> summonRegistry) {
      for (Class<? extends IMagicSummon> summonClass : summonRegistry) {
         if (summonClass.isAssignableFrom(entity.getClass())) {
            return true;
         }
      }

      return false;
   }
}
