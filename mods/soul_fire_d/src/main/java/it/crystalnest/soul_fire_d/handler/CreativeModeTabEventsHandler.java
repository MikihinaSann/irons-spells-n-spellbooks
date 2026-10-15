package it.crystalnest.soul_fire_d.handler;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.Fire.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(
   modid = "soul_fire_d",
   bus = Bus.MOD
)
public final class CreativeModeTabEventsHandler {
   @SubscribeEvent
   public static void handle(BuildCreativeModeTabContentsEvent event) {
      ResourceKey<CreativeModeTab> key = event.getTabKey();
      if (key == CreativeModeTabs.INGREDIENTS || key == CreativeModeTabs.TOOLS_AND_UTILITIES) {
         event.insertAfter(
            Items.FIRE_CHARGE.getDefaultInstance(),
            ((FireChargeItem)FireManager.getRequiredComponent(FireManager.SOUL_FIRE_TYPE, Component.FIRE_CHARGE_ITEM)).getDefaultInstance(),
            TabVisibility.PARENT_AND_SEARCH_TABS
         );
      }
   }
}
