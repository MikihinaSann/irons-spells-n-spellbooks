package com.somake.somakespells.event;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.config.Config;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public final class SoulBreakerBalanceHandler {
   private SoulBreakerBalanceHandler() {
   }

   @SubscribeEvent
   public static void onBeforeDamageTaken(Pre event) {
      if ((Boolean)Config.DISABLE_SOUL_BREAKER_BONUS_SOUL_DAMAGE.get()) {
         if (MagicFromTheEastCompat.isLoaded() && event.getSource().is(MagicFromTheEastCompat.SOUL_WEAPONS_BONUS)) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
               if (MagicFromTheEastCompat.isSoulBreaker(attacker.getMainHandItem())) {
                  event.setNewDamage(0.0F);
               }
            }
         }
      }
   }
}
