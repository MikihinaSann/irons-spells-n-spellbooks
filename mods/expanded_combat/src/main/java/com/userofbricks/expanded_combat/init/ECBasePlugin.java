package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.registry.ECPlugin;
import com.userofbricks.expanded_combat.api.registry.IExpandedCombatPlugin;
import com.userofbricks.expanded_combat.api.registry.RegistrationHandler;
import com.userofbricks.expanded_combat.api.weapon_type.WeaponType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@ECPlugin
public class ECBasePlugin implements IExpandedCombatPlugin {
   public static WeaponType BATTLE_STAFF;
   public static WeaponType BROAD_SWORD;
   public static WeaponType CLAYMORE;
   public static WeaponType CUTLASS;
   public static WeaponType DAGGER;
   public static WeaponType DANCERS_SWORD;
   public static WeaponType FLAIL;
   public static WeaponType GLAIVE;
   public static WeaponType GREAT_HAMMER;
   public static WeaponType KATANA;
   public static WeaponType MACE;
   public static WeaponType SCYTHE;
   public static WeaponType SICKLE;
   public static WeaponType SPEAR;
   public static Material LEATHER;
   public static Material RABBIT_HIDE;
   public static Material WOOD_PLANK;
   public static Material STONE;
   public static Material IRON;
   public static Material GOLD;
   public static Material DIAMOND;
   public static Material NETHERITE;
   public static Material HEART_STEALER;
   public static Material BRAWLERS;
   public static Material BERSERK;
   public static Material FIGHTER;
   public static Material HEAT_MATERIAL;
   public static Material FROST;
   public static Material VOID_TOUCHED;
   public static Material SOUL_MATERIAL;

   @Override
   public ResourceLocation getPluginUid() {
      return ExpandedCombat.modLoc("base");
   }

   @Override
   public void registerMaterials(RegistrationHandler registrationHandler) {
      BATTLE_STAFF = registrationHandler.registerWeaponType("Battle Staff", ExpandedCombat.modLoc("battle_staff"), ExpandedCombat.CONFIG.battlestaff);
      BROAD_SWORD = registrationHandler.registerWeaponType("Broad Sword", ExpandedCombat.modLoc("broad_sword"), ExpandedCombat.CONFIG.broadsword);
      CLAYMORE = registrationHandler.registerWeaponType("Claymore", ExpandedCombat.modLoc("claymore"), ExpandedCombat.CONFIG.claymore);
      CUTLASS = registrationHandler.registerWeaponType("Cutlass", ExpandedCombat.modLoc("cutlass"), ExpandedCombat.CONFIG.cutlass);
      DAGGER = registrationHandler.registerWeaponType("Dagger", ExpandedCombat.modLoc("dagger"), ExpandedCombat.CONFIG.dagger);
      DANCERS_SWORD = registrationHandler.registerWeaponType("Dancer's Sword", ExpandedCombat.modLoc("dancer_s_sword"), ExpandedCombat.CONFIG.dancers_sword);
      FLAIL = registrationHandler.registerBlockWeaponType("Flail", ExpandedCombat.modLoc("flail"), ExpandedCombat.CONFIG.flail);
      GLAIVE = registrationHandler.registerWeaponType("Glaive", ExpandedCombat.modLoc("glaive"), ExpandedCombat.CONFIG.glaive);
      GREAT_HAMMER = registrationHandler.registerBlockWeaponType("Great Hammer", ExpandedCombat.modLoc("great_hammer"), ExpandedCombat.CONFIG.great_hammer);
      KATANA = registrationHandler.registerWeaponType("Katana", ExpandedCombat.modLoc("katana"), ExpandedCombat.CONFIG.katana);
      MACE = registrationHandler.registerBlockWeaponType("Mace", ExpandedCombat.modLoc("mace"), ExpandedCombat.CONFIG.mace);
      SCYTHE = registrationHandler.registerPotionWeaponType("Scythe", ExpandedCombat.modLoc("scythe"), ExpandedCombat.CONFIG.scythe);
      SICKLE = registrationHandler.registerWeaponType("Sickle", ExpandedCombat.modLoc("sickle"), ExpandedCombat.CONFIG.sickle);
      SPEAR = registrationHandler.registerWeaponType("Spear", ExpandedCombat.modLoc("spear"), ExpandedCombat.CONFIG.spear);
      LEATHER = registrationHandler.registerMaterial(
         "Leather",
         ExpandedCombat.modLoc("leather"),
         ExpandedCombat.CONFIG.leather,
         () -> Ingredient.of(ECTags.LEATHER_REPAIR),
         () -> Ingredient.of(new ItemLike[]{Items.LEATHER})
      );
      RABBIT_HIDE = registrationHandler.registerMaterial(
         "Rabbit Hide",
         ExpandedCombat.modLoc("rabbit_hide"),
         ExpandedCombat.CONFIG.rebbitLeather,
         () -> Ingredient.of(ECTags.RABBIT_HIDE_REPAIR),
         () -> Ingredient.of(new ItemLike[]{Items.RABBIT_HIDE})
      );
      WOOD_PLANK = registrationHandler.registerMaterial(
         "Wood Plank",
         ExpandedCombat.modLoc("wood_plank"),
         ExpandedCombat.CONFIG.woodPlank,
         () -> Ingredient.of(ItemTags.PLANKS),
         () -> Ingredient.of(ItemTags.PLANKS)
      );
      STONE = registrationHandler.registerMaterial(
         "Stone",
         ExpandedCombat.modLoc("stone"),
         ExpandedCombat.CONFIG.stone,
         () -> Ingredient.of(ItemTags.STONE_TOOL_MATERIALS),
         () -> Ingredient.of(ItemTags.STONE_TOOL_MATERIALS)
      );
      IRON = registrationHandler.registerMaterial(
         "Iron",
         ExpandedCombat.modLoc("iron"),
         ExpandedCombat.CONFIG.iron,
         () -> Ingredient.of(ECTags.IRON_REPAIR),
         () -> Ingredient.of(new ItemLike[]{Items.IRON_INGOT})
      );
      GOLD = registrationHandler.registerMaterial(
         "Gold",
         ExpandedCombat.modLoc("gold"),
         ExpandedCombat.CONFIG.gold,
         () -> Ingredient.of(ECTags.GOLD_REPAIR),
         () -> Ingredient.of(new ItemLike[]{Items.GOLD_INGOT})
      );
      DIAMOND = registrationHandler.registerMaterial(
         "Diamond",
         ExpandedCombat.modLoc("diamond"),
         ExpandedCombat.CONFIG.diamond,
         () -> Ingredient.of(ECTags.DIAMOND_REPAIR),
         () -> Ingredient.of(new ItemLike[]{Items.DIAMOND})
      );
      NETHERITE = registrationHandler.registerMaterial(
         "Netherite",
         ExpandedCombat.modLoc("netherite"),
         ExpandedCombat.CONFIG.netherite,
         () -> Ingredient.of(ECTags.NETHERITE_REPAIR),
         () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_INGOT}),
         () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE})
      );
      HEART_STEALER = registrationHandler.registerMaterial("Heart Stealer", ExpandedCombat.modLoc("heart_stealer"), ExpandedCombat.CONFIG.heartStealer);
      HEAT_MATERIAL = registrationHandler.registerMaterial("Heat", ExpandedCombat.modLoc("heat"), ExpandedCombat.CONFIG.heat);
      FROST = registrationHandler.registerMaterial("Frost", ExpandedCombat.modLoc("frost"), ExpandedCombat.CONFIG.frost);
      VOID_TOUCHED = registrationHandler.registerMaterial("Void Touched", ExpandedCombat.modLoc("void_touched"), ExpandedCombat.CONFIG.voidTouched);
      SOUL_MATERIAL = registrationHandler.registerMaterial("Soul", ExpandedCombat.modLoc("soul"), ExpandedCombat.CONFIG.soul);
      FIGHTER = registrationHandler.registerMaterial("Fighters", ExpandedCombat.modLoc("fighters"), ExpandedCombat.CONFIG.fighters);
      BERSERK = registrationHandler.registerMaterial("Berserk", ExpandedCombat.modLoc("berserk"), ExpandedCombat.CONFIG.maulers);
      BRAWLERS = registrationHandler.registerMaterial("Brawlers", ExpandedCombat.modLoc("brawlers"), ExpandedCombat.CONFIG.gauntlet);
   }

   @Override
   public int loadOrder() {
      return 0;
   }
}
