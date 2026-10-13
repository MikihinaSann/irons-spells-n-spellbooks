package net.veroxuniverse.crystal_chronicles.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class CCEvents {
   @EventBusSubscriber(
      modid = "crystal_chronicles"
   )
   public static class RegistrationHandler {
      @SubscribeEvent
      public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
      }

      @SubscribeEvent
      public static void entitySpawnRestriction(RegisterSpawnPlacementsEvent event) {
      }
   }
}
