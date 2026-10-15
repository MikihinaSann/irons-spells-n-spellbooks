package com.userofbricks.ecisasbplugin;

import com.userofbricks.ecisasbplugin.config.ECISaSbConfig;
import com.userofbricks.ecisasbplugin.item.ECISaSbItems;
import com.userofbricks.expanded_combat.item.GauntletItem;
import com.userofbricks.expanded_combat.item.QuiverItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod("ec_isasb_plugin")
public class ECISaSbPlugin {
   public static final String MODID = "ec_isasb_plugin";
   public static ECISaSbConfig CONFIG;

   public ECISaSbPlugin(IEventBus bus, ModContainer modContainer) {
      bus.addListener(this::clientSetup);
      ECISaSbItems.ITEMS.register(bus);
   }

   public static ResourceLocation modLoc(String path) {
      return ResourceLocation.fromNamespaceAndPath("ec_isasb_plugin", path);
   }

   private void clientSetup(FMLClientSetupEvent event) {
      for (DeferredItem<? extends Item> registryEntry : ECISaSbItems.ITEMS
         .getEntries()
         .stream()
         .map(itemDeferredHolder -> (DeferredItem)itemDeferredHolder)
         .toList()) {
         if (registryEntry.get() instanceof GauntletItem gauntletItem) {
            CuriosRendererRegistry.register(gauntletItem, gauntletItem.getGauntletRenderer());
         } else if (registryEntry.get() instanceof QuiverItem quiverItem) {
            CuriosRendererRegistry.register(quiverItem, quiverItem.getQuiverRenderer());
         }
      }
   }
}
