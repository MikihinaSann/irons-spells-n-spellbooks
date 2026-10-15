package com.userofbricks.ecisasbplugin.plugins;

import com.userofbricks.ecisasbplugin.ECISaSbPlugin;
import com.userofbricks.ecisasbplugin.config.ECISaSbConfig;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.registry.ECPlugin;
import com.userofbricks.expanded_combat.api.registry.IExpandedCombatPlugin;
import com.userofbricks.expanded_combat.api.registry.RegistrationHandler;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@ECPlugin
public class IronsSpellsAndSpellbooksPlugin implements IExpandedCombatPlugin {
   public static Material WANDERING_MAGICIAN;
   public static Material SCARECROW;
   public static Material PYROMANCER;
   public static Material ELECTROMANCER;
   public static Material ARCHEVOKER;
   public static Material CULTIST;
   public static Material CRYOMANCER;
   public static Material SHADOW_WALKER;
   public static Material PRIEST;
   public static Material PLAGUED;
   public static Material NETHERITE_BATTLEMAGE;

   public ResourceLocation getPluginUid() {
      return ECISaSbPlugin.modLoc("irons_spellbooks");
   }

   public void registerMaterials(RegistrationHandler registrationHandler) {
      AutoConfig.register(ECISaSbConfig.class, Toml4jConfigSerializer::new);
      ECISaSbPlugin.CONFIG = (ECISaSbConfig)AutoConfig.getConfigHolder(ECISaSbConfig.class).getConfig();
      WANDERING_MAGICIAN = registrationHandler.registerMaterial(
         "Wandering Magician",
         ECISaSbPlugin.modLoc("wandering_magician"),
         ECISaSbPlugin.CONFIG.wanderingMagician,
         () -> Ingredient.of(new ItemLike[]{Items.LEATHER}),
         null
      );
      SCARECROW = registrationHandler.registerMaterial(
         "Scarecrow", ECISaSbPlugin.modLoc("scarecrow"), ECISaSbPlugin.CONFIG.scarecrow, () -> Ingredient.of(new ItemLike[]{Items.HAY_BLOCK}), null
      );
      PYROMANCER = registrationHandler.registerMaterial(
         "Pyromancer",
         ECISaSbPlugin.modLoc("pyromancer"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      ELECTROMANCER = registrationHandler.registerMaterial(
         "Electromancer",
         ECISaSbPlugin.modLoc("electromancer"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      ARCHEVOKER = registrationHandler.registerMaterial(
         "Archevoker",
         ECISaSbPlugin.modLoc("archevoker"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      CULTIST = registrationHandler.registerMaterial(
         "Cultist",
         ECISaSbPlugin.modLoc("cultist"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      CRYOMANCER = registrationHandler.registerMaterial(
         "Cryomancer",
         ECISaSbPlugin.modLoc("cryomancer"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      SHADOW_WALKER = registrationHandler.registerMaterial(
         "Shadow Walker",
         ECISaSbPlugin.modLoc("shadow_walker"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      PRIEST = registrationHandler.registerMaterial(
         "Priest",
         ECISaSbPlugin.modLoc("priest"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      PLAGUED = registrationHandler.registerMaterial(
         "Plagued",
         ECISaSbPlugin.modLoc("plagued"),
         ECISaSbPlugin.CONFIG.magicClothes,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
         null
      );
      NETHERITE_BATTLEMAGE = registrationHandler.registerMaterial(
         "Netherite Battlemage",
         ECISaSbPlugin.modLoc("netherite_battlemage"),
         ECISaSbPlugin.CONFIG.netheriteBattlemage,
         () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_INGOT}),
         null
      );
   }
}
