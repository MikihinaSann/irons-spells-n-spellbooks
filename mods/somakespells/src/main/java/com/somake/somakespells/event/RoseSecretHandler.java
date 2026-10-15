package com.somake.somakespells.event;

import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public final class RoseSecretHandler {
   private RoseSecretHandler() {
   }

   @SubscribeEvent
   public static void onBeforeDamageTaken(Pre event) {
      LivingEntity livingEntity = event.getEntity();
      if (livingEntity.hasEffect(ModMobEffects.ROSE_SECRET)) {
         event.setNewDamage(0.0F);
      }
   }
}
