package com.chadate.spellelemental.register;

import com.chadate.spellelemental.item.SwordOilItem;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "spellelemental");
   public static final Supplier<Item> SWORD_STAND = ITEMS.register(
      "sword_stand", () -> new BlockItem(ModBlocks.SWORD_STAND.get(), new Properties().rarity(Rarity.COMMON))
   );
   public static final Supplier<Item> FIRE_SWORD_OIL = ITEMS.register(
      "fire_sword_oil", () -> new SwordOilItem(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final Supplier<Item> ICE_SWORD_OIL = ITEMS.register(
      "ice_sword_oil", () -> new SwordOilItem(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final Supplier<Item> LIGHTNING_SWORD_OIL = ITEMS.register(
      "lightning_sword_oil", () -> new SwordOilItem(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final Supplier<Item> BLOOD_SWORD_OIL = ITEMS.register(
      "blood_sword_oil", () -> new SwordOilItem(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final Supplier<Item> NATURE_SWORD_OIL = ITEMS.register(
      "nature_sword_oil", () -> new SwordOilItem(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON))
   );
   public static final Supplier<Item> STELLAR_FRAGMENT = ITEMS.register("stellar_fragment", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final Supplier<Item> STELLAR_FRAGMENT_ORE = ITEMS.register(
      "stellar_fragment_ore", () -> new BlockItem(ModBlocks.STELLAR_FRAGMENT_ORE.get(), new Properties().rarity(Rarity.COMMON))
   );
   public static final Supplier<Item> DEEPSLATE_STELLAR_FRAGMENT_ORE = ITEMS.register(
      "deepslate_stellar_fragment_ore", () -> new BlockItem(ModBlocks.DEEPSLATE_STELLAR_FRAGMENT_ORE.get(), new Properties().rarity(Rarity.COMMON))
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
