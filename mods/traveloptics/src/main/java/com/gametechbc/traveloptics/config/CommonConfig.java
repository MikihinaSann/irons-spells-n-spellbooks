package com.gametechbc.traveloptics.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD
)
public class CommonConfig {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue showWelcomeMessage = BUILDER.comment("Show welcome message || Default: true").define("general.welcome_message", true);
   public static final ModConfigSpec SPEC = BUILDER.build();

   public CommonConfig() {
      super();
   }

   private static boolean validateItemName(Object obj) {
      return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
   }

   @SubscribeEvent
   static void onLoad(ModConfigEvent event) {
   }
}
