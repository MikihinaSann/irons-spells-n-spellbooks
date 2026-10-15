package com.legacy.dash;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("dash")
public class DashMod {
   public static final String NAME = "Dash";
   public static final String MODID = "dash";
   public static final String PACKET_VERSION = "1.2.0";

   public static ResourceLocation locate(String name) {
      return ResourceLocation.fromNamespaceAndPath("dash", name);
   }

   public static String find(String name) {
      return "dash:" + name;
   }

   public DashMod(IEventBus modBus, ModContainer modContainer) {
      modContainer.registerConfig(Type.SERVER, DashConfig.SERVER_SPEC);
      if (FMLEnvironment.dist == Dist.CLIENT) {
         NeoForge.EVENT_BUS.register(new DashEntityEvents());
         modBus.addListener(DashMod.DashClient::registerKeys);
      }
   }

   static class DashClient {
      public static KeyMapping DASH_KEYBIND = new KeyMapping("dash.key.dash", 82, "key.categories.movement");

      @SubscribeEvent
      public static void registerKeys(RegisterKeyMappingsEvent event) {
         event.register(DASH_KEYBIND);
      }
   }
}
