package com.chadate.spellelemental.integration.jei.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(
   modid = "spellelemental"
)
public class SwordOilDataManager {
   private static SwordOilConfigLoader configLoader;

   @SubscribeEvent
   public static void onAddReloadListeners(AddReloadListenerEvent event) {
      configLoader = new SwordOilConfigLoader();
      event.addListener(configLoader);
   }

   public static SwordOilConfigLoader getConfigLoader() {
      return configLoader;
   }
}
