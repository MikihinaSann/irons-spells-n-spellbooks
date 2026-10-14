package com.gametechbc.traveloptics.init;

import com.gametechbc.traveloptics.item.wildaugment.TestWildAugment;
import java.util.Collection;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOWildAugments {
   private static final DeferredRegister<Item> WILD_AUGMENTS = DeferredRegister.create(Registries.ITEM, "traveloptics");
   public static final DeferredHolder<Item, Item> AUGMENT_TEST_WILD = WILD_AUGMENTS.register(
      "augment_test_wild", () -> new TestWildAugment(new Properties().rarity(Rarity.RARE))
   );

   public TOWildAugments() {
      super();
   }

   public static void register(IEventBus eventBus) {
      WILD_AUGMENTS.register(eventBus);
   }

   public static Collection<DeferredHolder<Item, ? extends Item>> getIronsItems() {
      return WILD_AUGMENTS.getEntries();
   }
}
