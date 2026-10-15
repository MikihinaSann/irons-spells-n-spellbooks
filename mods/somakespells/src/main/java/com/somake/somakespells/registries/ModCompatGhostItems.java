package com.somake.somakespells.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCompatGhostItems {
   public static final DeferredRegister<Item> GEO_ITEMS = DeferredRegister.create(Registries.ITEM, "somakespells");
   public static final DeferredRegister<Item> SOUND_ITEMS = DeferredRegister.create(Registries.ITEM, "somakespells");
   public static final DeferredHolder<Item, Item> GEO_GHOST_SPAWN_EGG = GEO_ITEMS.register(
      "geo_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModCompatEntityTypes.GEO_GHOST, 4086330, 10408090, new Properties())
   );
   public static final DeferredHolder<Item, Item> SOUND_GHOST_SPAWN_EGG = SOUND_ITEMS.register(
      "sound_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModCompatEntityTypes.SOUND_GHOST, 6967224, 13678847, new Properties())
   );

   public static void registerGeo(IEventBus eventBus) {
      GEO_ITEMS.register(eventBus);
   }

   public static void registerSound(IEventBus eventBus) {
      SOUND_ITEMS.register(eventBus);
   }
}
