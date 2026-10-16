package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.item.AllayItem;
import com.userofbricks.expanded_combat.item.ArrowBlockWeaponItem;
import com.userofbricks.expanded_combat.item.ECArrowItem;
import com.userofbricks.expanded_combat.item.ECBowItem;
import com.userofbricks.expanded_combat.item.ECCrossBowItem;
import com.userofbricks.expanded_combat.item.ECShieldItem;
import com.userofbricks.expanded_combat.item.ECTippedArrowItem;
import com.userofbricks.expanded_combat.item.ECWeaponItem;
import com.userofbricks.expanded_combat.item.ElementalWeapon;
import com.userofbricks.expanded_combat.item.GauntletBerserk;
import com.userofbricks.expanded_combat.item.GauntletBrawlers;
import com.userofbricks.expanded_combat.item.GauntletItem;
import com.userofbricks.expanded_combat.item.HeartStealerItem;
import com.userofbricks.expanded_combat.item.PotionWeaponItem;
import com.userofbricks.expanded_combat.item.PurifiedGasBottle;
import com.userofbricks.expanded_combat.item.QuiverItem;
import com.userofbricks.expanded_combat.item.SlamWeaponItem;
import com.userofbricks.expanded_combat.item.SolidPureFoodItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ECItems {
   public static final Items ITEMS = DeferredRegister.createItems("expanded_combat");
   public static final DeferredItem<Item> LEATHER_STICK = ITEMS.registerSimpleItem("leather_stick");
   public static final DeferredItem<Item> GOLD_STICK = ITEMS.registerSimpleItem("gold_stick");
   public static final DeferredItem<Item> IRON_STICK = ITEMS.registerSimpleItem("iron_stick");
   public static final DeferredItem<Item> FLETCHED_STICKS = ITEMS.registerSimpleItem("fletched_sticks");
   public static final DeferredItem<PurifiedGasBottle> GAS_BOTTLE = ITEMS.registerItem(
      "gas_bottle", properties -> new PurifiedGasBottle(properties, ECBlocks.GAS_BLOCK::get)
   );
   public static final DeferredItem<PurifiedGasBottle> PURIFIED_GAS_BOTTLE = ITEMS.registerItem(
      "purified_gas_bottle", properties -> new PurifiedGasBottle(properties, ECBlocks.PURIFIED_GAS_BLOCK::get)
   );
   public static final DeferredItem<SolidPureFoodItem> SOLIDIFIED_PURIFICATION = ITEMS.registerItem(
      "solidified_purification",
      properties -> new SolidPureFoodItem(properties.food(new Builder().alwaysEdible().nutrition(0).saturationModifier(0.0F).build()))
   );
   public static final DeferredItem<Item> BAD_SOUL = ITEMS.registerSimpleItem("evil_soul");
   public static final DeferredItem<Item> GOOD_SOUL = ITEMS.registerSimpleItem("good_soul");
   public static final DeferredItem<AllayItem> ALLAY_ITEM = ITEMS.registerItem("allay", AllayItem::new);
   public static final DeferredItem<ECShieldItem> SHIELD = ITEMS.registerItem("shield", ECShieldItem::new);
   public static final DeferredItem<ECShieldItem> SHIELD_FIRE_RESISTANT = ITEMS.registerItem(
      "fire_resistant_shield", ECShieldItem::new, () -> new Properties().fireResistant()
   );
   public static final DeferredItem<GauntletItem> LEATHER_GAUNTLET = ITEMS.registerItem(
      "leather_gauntlet", properties -> new GauntletItem(properties, ECBasePlugin.LEATHER, new GauntletItem.Layer(true), new GauntletItem.Layer("overlay"))
   );
   public static final DeferredItem<QuiverItem> LEATHER_QUIVER = ITEMS.registerItem(
      "leather_quiver", properties -> new QuiverItem(properties, ECBasePlugin.LEATHER)
   );
   public static final DeferredItem<GauntletItem> RABBIT_HIDE_GAUNTLET = ITEMS.registerItem(
      "rabbit_hide_gauntlet", properties -> new GauntletItem(properties, ECBasePlugin.RABBIT_HIDE)
   );
   public static final DeferredItem<QuiverItem> RABBIT_HIDE_QUIVER = ITEMS.registerItem(
      "rabbit_hide_quiver", properties -> new QuiverItem(properties, ECBasePlugin.RABBIT_HIDE)
   );
   public static final List<DeferredItem<? extends ECWeaponItem>> WOOD_PLANK_WEAPONS = standardWeaponsFor(ECBasePlugin.WOOD_PLANK, "wooden");
   public static final List<DeferredItem<? extends ECWeaponItem>> STONE_WEAPONS = standardWeaponsFor(ECBasePlugin.STONE, "stone");
   public static final List<DeferredItem<? extends ECWeaponItem>> IRON_WEAPONS = standardWeaponsFor(ECBasePlugin.IRON, "iron");
   public static final DeferredItem<? extends ECArrowItem> IRON_ARROW = ITEMS.registerItem(
      "iron_arrow", properties -> new ECArrowItem(properties, ECBasePlugin.IRON)
   );
   public static final DeferredItem<? extends ECArrowItem> IRON_TIPPED_ARROW = ITEMS.registerItem(
      "tipped_iron_arrow", properties -> new ECTippedArrowItem(properties, ECBasePlugin.IRON, IRON_ARROW)
   );
   public static final DeferredItem<? extends ECBowItem> IRON_BOW = ITEMS.registerItem("iron_bow", properties -> new ECBowItem(properties, ECBasePlugin.IRON));
   public static final DeferredItem<? extends ECCrossBowItem> IRON_CROSS_BOW = ITEMS.registerItem(
      "iron_cross_bow", properties -> new ECCrossBowItem(properties, ECBasePlugin.IRON)
   );
   public static final DeferredItem<? extends GauntletItem> IRON_GAUNTLET = ITEMS.registerItem(
      "iron_gauntlet", properties -> new GauntletItem(properties, ECBasePlugin.IRON)
   );
   public static final DeferredItem<? extends QuiverItem> IRON_QUIVER = ITEMS.registerItem(
      "iron_quiver", properties -> new QuiverItem(properties, ECBasePlugin.IRON)
   );
   public static final List<DeferredItem<? extends ECWeaponItem>> GOLD_WEAPONS = standardWeaponsFor(ECBasePlugin.GOLD, "golden");
   public static final DeferredItem<? extends ECBowItem> GOLD_BOW = ITEMS.registerItem("golden_bow", properties -> new ECBowItem(properties, ECBasePlugin.GOLD));
   public static final DeferredItem<? extends ECCrossBowItem> GOLD_CROSS_BOW = ITEMS.registerItem(
      "golden_cross_bow", properties -> new ECCrossBowItem(properties, ECBasePlugin.GOLD)
   );
   public static final DeferredItem<? extends GauntletItem> GOLD_GAUNTLET = ITEMS.registerItem(
      "golden_gauntlet", properties -> new GauntletItem(properties, ECBasePlugin.GOLD)
   );
   public static final DeferredItem<? extends QuiverItem> GOLD_QUIVER = ITEMS.registerItem(
      "golden_quiver", properties -> new QuiverItem(properties, ECBasePlugin.GOLD)
   );
   public static final List<DeferredItem<? extends ECWeaponItem>> DIAMOND_WEAPONS = standardWeaponsFor(ECBasePlugin.DIAMOND, "diamond");
   public static final DeferredItem<? extends ECArrowItem> DIAMOND_ARROW = ITEMS.registerItem(
      "diamond_arrow", properties -> new ECArrowItem(properties, ECBasePlugin.DIAMOND)
   );
   public static final DeferredItem<? extends ECArrowItem> DIAMOND_TIPPED_ARROW = ITEMS.registerItem(
      "tipped_diamond_arrow", properties -> new ECTippedArrowItem(properties, ECBasePlugin.DIAMOND, DIAMOND_ARROW)
   );
   public static final DeferredItem<? extends ECBowItem> DIAMOND_BOW = ITEMS.registerItem(
      "diamond_bow", properties -> new ECBowItem(properties, ECBasePlugin.DIAMOND)
   );
   public static final DeferredItem<? extends ECCrossBowItem> DIAMOND_CROSS_BOW = ITEMS.registerItem(
      "diamond_cross_bow", properties -> new ECCrossBowItem(properties, ECBasePlugin.DIAMOND)
   );
   public static final DeferredItem<? extends GauntletItem> DIAMOND_GAUNTLET = ITEMS.registerItem(
      "diamond_gauntlet", properties -> new GauntletItem(properties, ECBasePlugin.DIAMOND)
   );
   public static final DeferredItem<? extends QuiverItem> DIAMOND_QUIVER = ITEMS.registerItem(
      "diamond_quiver", properties -> new QuiverItem(properties, ECBasePlugin.DIAMOND)
   );
   public static final List<DeferredItem<? extends ECWeaponItem>> NETHERITE_WEAPONS = standardWeaponsFor(ECBasePlugin.NETHERITE, "netherite");
   public static final DeferredItem<? extends ECArrowItem> NETHERITE_ARROW = ITEMS.registerItem(
      "netherite_arrow", properties -> new ECArrowItem(properties, ECBasePlugin.NETHERITE)
   );
   public static final DeferredItem<? extends ECArrowItem> NETHERITE_TIPPED_ARROW = ITEMS.registerItem(
      "tipped_netherite_arrow", properties -> new ECTippedArrowItem(properties, ECBasePlugin.NETHERITE, NETHERITE_ARROW)
   );
   public static final DeferredItem<? extends ECBowItem> NETHERITE_BOW = ITEMS.registerItem(
      "netherite_bow", properties -> new ECBowItem(properties, ECBasePlugin.NETHERITE)
   );
   public static final DeferredItem<? extends ECCrossBowItem> NETHERITE_CROSS_BOW = ITEMS.registerItem(
      "netherite_cross_bow", properties -> new ECCrossBowItem(properties, ECBasePlugin.NETHERITE)
   );
   public static final DeferredItem<? extends GauntletItem> NETHERITE_GAUNTLET = ITEMS.registerItem(
      "netherite_gauntlet", properties -> new GauntletItem(properties, ECBasePlugin.NETHERITE)
   );
   public static final DeferredItem<? extends QuiverItem> NETHERITE_QUIVER = ITEMS.registerItem(
      "netherite_quiver", properties -> new QuiverItem(properties, ECBasePlugin.NETHERITE)
   );
   public static final DeferredItem<HeartStealerItem> HEART_STEALER = ITEMS.registerItem("heart_stealer", HeartStealerItem::new);
   public static final DeferredItem<ElementalWeapon> HEAT_KATANA = ITEMS.registerItem(
      "sun_master_s_katana", properties -> new ElementalWeapon(ECBasePlugin.HEAT_MATERIAL, ECBasePlugin.KATANA, properties, 2, ECAttributes.HEAT_DMG)
   );
   public static final DeferredItem<ElementalWeapon> HEAT_MACE = ITEMS.registerItem(
      "sun_s_firebrand", properties -> new ElementalWeapon(ECBasePlugin.HEAT_MATERIAL, ECBasePlugin.MACE, properties, 2, ECAttributes.HEAT_DMG)
   );
   public static final DeferredItem<ElementalWeapon> HEAT_SCYTHE = ITEMS.registerItem(
      "sun_s_grace", properties -> new ElementalWeapon(ECBasePlugin.HEAT_MATERIAL, ECBasePlugin.SCYTHE, properties, 2, ECAttributes.HEAT_DMG)
   );
   public static final DeferredItem<ElementalWeapon> HEAT_GLAIVE = ITEMS.registerItem(
      "grave_bane", properties -> new ElementalWeapon(ECBasePlugin.HEAT_MATERIAL, ECBasePlugin.GLAIVE, properties, 2, ECAttributes.HEAT_DMG)
   );
   public static final DeferredItem<ElementalWeapon> FROST_DAGGER = ITEMS.registerItem(
      "fang_of_frost", properties -> new ElementalWeapon(ECBasePlugin.FROST, ECBasePlugin.DAGGER, properties, 2, ECAttributes.COLD_DMG)
   );
   public static final DeferredItem<ElementalWeapon> FROST_SCYTHE = ITEMS.registerItem(
      "frost_scythe", properties -> new ElementalWeapon(ECBasePlugin.FROST, ECBasePlugin.SCYTHE, properties, 2, ECAttributes.COLD_DMG)
   );
   public static final DeferredItem<ElementalWeapon> FROST_CLAYMORE = ITEMS.registerItem(
      "frost_slayer", properties -> new ElementalWeapon(ECBasePlugin.FROST, ECBasePlugin.CLAYMORE, properties, 2, ECAttributes.COLD_DMG)
   );
   public static final DeferredItem<ElementalWeapon> VOID_TOUCHED_CLAYMORE = ITEMS.registerItem(
      "void_touched_claymore", properties -> new ElementalWeapon(ECBasePlugin.VOID_TOUCHED, ECBasePlugin.CLAYMORE, properties, 2, ECAttributes.VOID_DMG)
   );
   public static final DeferredItem<ElementalWeapon> VOID_TOUCHED_DAGGER = ITEMS.registerItem(
      "void_touched_blade", properties -> new ElementalWeapon(ECBasePlugin.VOID_TOUCHED, ECBasePlugin.DAGGER, properties, 2, ECAttributes.VOID_DMG)
   );
   public static final DeferredItem<ElementalWeapon> VOID_TOUCHED_CUTLASS = ITEMS.registerItem(
      "nameless_blade", properties -> new ElementalWeapon(ECBasePlugin.VOID_TOUCHED, ECBasePlugin.CUTLASS, properties, 2, ECAttributes.VOID_DMG)
   );
   public static final DeferredItem<ElementalWeapon> VOID_TOUCHED_GREAT_HAMMER = ITEMS.registerItem(
      "void_touched_great_hammer",
      properties -> new ElementalWeapon(ECBasePlugin.VOID_TOUCHED, ECBasePlugin.GREAT_HAMMER, properties, 2, ECAttributes.VOID_DMG)
   );
   public static final DeferredItem<ElementalWeapon> SOUL_KATANA = ITEMS.registerItem(
      "dark_katana", properties -> new ElementalWeapon(ECBasePlugin.SOUL_MATERIAL, ECBasePlugin.KATANA, properties, 2, ECAttributes.SOUL_DMG)
   );
   public static final DeferredItem<ElementalWeapon> SOUL_DAGGER = ITEMS.registerItem(
      "eternal_soul_knife", properties -> new ElementalWeapon(ECBasePlugin.SOUL_MATERIAL, ECBasePlugin.DAGGER, properties, 2, ECAttributes.SOUL_DMG)
   );
   public static final DeferredItem<ElementalWeapon> SOUL_SCYTHE = ITEMS.registerItem(
      "soul_scythe", properties -> new ElementalWeapon(ECBasePlugin.SOUL_MATERIAL, ECBasePlugin.SCYTHE, properties, 2, ECAttributes.SOUL_DMG)
   );
   public static final DeferredItem<GauntletItem> SOUL_GAUNTLET = ITEMS.registerItem(
      "soul_fist", properties -> new GauntletItem(properties, ECBasePlugin.SOUL_MATERIAL, new GauntletItem.Layer(ExpandedCombat.modLoc("soul_fist"), false))
   );
   public static final DeferredItem<GauntletItem> FIGHTERS_GAUNTLETS = ITEMS.registerItem(
      "fighter_s_gauntlet",
      properties -> new GauntletItem(properties, ECBasePlugin.FIGHTER, new GauntletItem.Layer(ExpandedCombat.modLoc("fighters_bindings"), false))
   );
   public static final DeferredItem<GauntletItem> BERSERK_GAUNTLETS = ITEMS.registerItem(
      "berserk_gauntlet",
      properties -> new GauntletBerserk(properties, ECBasePlugin.BERSERK, new GauntletItem.Layer(ExpandedCombat.modLoc("berserk_gauntlet"), false))
   );
   public static final DeferredItem<GauntletItem> BRAWLERS_GAUNTLETS = ITEMS.registerItem(
      "brawler_s_gauntlet",
      properties -> new GauntletBrawlers(properties, ECBasePlugin.BRAWLERS, new GauntletItem.Layer(ExpandedCombat.modLoc("brawlers_gauntlet"), false))
   );

   private static List<DeferredItem<? extends ECWeaponItem>> standardWeaponsFor(Material materialReference, String materialItemName) {
      List<DeferredItem<? extends ECWeaponItem>> weaponMap = new ArrayList<>();
      weaponMap.add(
         ITEMS.registerItem(materialItemName + "_battle_staff", properties -> new ECWeaponItem(materialReference, ECBasePlugin.BATTLE_STAFF, properties))
      );
      weaponMap.add(
         ITEMS.registerItem(materialItemName + "_broad_sword", properties -> new SlamWeaponItem(materialReference, ECBasePlugin.BROAD_SWORD, properties, 0))
      );
      weaponMap.add(
         ITEMS.registerItem(materialItemName + "_claymore", properties -> new SlamWeaponItem(materialReference, ECBasePlugin.CLAYMORE, properties, 0))
      );
      weaponMap.add(ITEMS.registerItem(materialItemName + "_cutlass", properties -> new ECWeaponItem(materialReference, ECBasePlugin.CUTLASS, properties)));
      weaponMap.add(ITEMS.registerItem(materialItemName + "_dagger", properties -> new ECWeaponItem(materialReference, ECBasePlugin.DAGGER, properties)));
      weaponMap.add(
         ITEMS.registerItem(materialItemName + "_dancer_s_sword", properties -> new ECWeaponItem(materialReference, ECBasePlugin.DANCERS_SWORD, properties))
      );
      weaponMap.add(ITEMS.registerItem(materialItemName + "_flail", properties -> new ECWeaponItem(materialReference, ECBasePlugin.FLAIL, properties)));
      weaponMap.add(ITEMS.registerItem(materialItemName + "_glaive", properties -> new ECWeaponItem(materialReference, ECBasePlugin.GLAIVE, properties)));
      weaponMap.add(
         ITEMS.registerItem(
            materialItemName + "_great_hammer",
            properties -> new SlamWeaponItem(
               materialReference, ECBasePlugin.GREAT_HAMMER, properties, ExpandedCombat.CONFIG.enchantmentLevels.baseHammerSlamLevel
            )
         )
      );
      weaponMap.add(
         ITEMS.registerItem(materialItemName + "_katana", properties -> new ArrowBlockWeaponItem(materialReference, ECBasePlugin.KATANA, properties, 2))
      );
      weaponMap.add(ITEMS.registerItem(materialItemName + "_mace", properties -> new ECWeaponItem(materialReference, ECBasePlugin.MACE, properties)));
      weaponMap.add(ITEMS.registerItem(materialItemName + "_scythe", properties -> new PotionWeaponItem(materialReference, ECBasePlugin.SCYTHE, properties)));
      weaponMap.add(ITEMS.registerItem(materialItemName + "_sickle", properties -> new ECWeaponItem(materialReference, ECBasePlugin.SICKLE, properties)));
      weaponMap.add(ITEMS.registerItem(materialItemName + "_spear", properties -> new ECWeaponItem(materialReference, ECBasePlugin.SPEAR, properties)));
      return weaponMap;
   }
}
