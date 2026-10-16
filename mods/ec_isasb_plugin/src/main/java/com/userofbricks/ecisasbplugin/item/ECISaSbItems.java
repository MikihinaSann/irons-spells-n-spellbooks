package com.userofbricks.ecisasbplugin.item;

import com.userofbricks.ecisasbplugin.plugins.IronsSpellsAndSpellbooksPlugin;
import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.init.ECCreativeTabs;
import com.userofbricks.expanded_combat.init.ECItems;
import com.userofbricks.expanded_combat.item.GauntletItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

@EventBusSubscriber(
   modid = "ec_isasb_plugin",
   bus = Bus.MOD
)
public class ECISaSbItems {
   public static final Items ITEMS = DeferredRegister.createItems("ec_isasb_plugin");
   public static final DeferredItem<? extends GauntletItem> WNADERING_MAGICIAN_GAUNTLET = ITEMS.registerItem(
      "wnadering_magician_gauntlet",
      properties -> new ExtraAttributesGauntlet(properties, IronsSpellsAndSpellbooksPlugin.WANDERING_MAGICIAN, ExtendedArmorItem.withManaAttribute(25))
   );
   public static final DeferredItem<? extends GauntletItem> SCARECROW_GAUNTLET = ITEMS.registerItem(
      "scarecrow_gauntlet",
      properties -> new ExtraAttributesGauntlet(properties, IronsSpellsAndSpellbooksPlugin.SCARECROW, ExtendedArmorItem.withManaAttribute(75))
   );
   public static final DeferredItem<? extends GauntletItem> PYROMANCER_GAUNTLET = ITEMS.registerItem(
      "pyromancer_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.PYROMANCER, ExtendedArmorItem.schoolAttributes(AttributeRegistry.FIRE_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> ELECTROMANCER_GAUNTLET = ITEMS.registerItem(
      "electromancer_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.ELECTROMANCER, ExtendedArmorItem.schoolAttributes(AttributeRegistry.LIGHTNING_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> ARCHEVOKER_GAUNTLET = ITEMS.registerItem(
      "archevoker_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.ARCHEVOKER, ExtendedArmorItem.schoolAttributes(AttributeRegistry.EVOCATION_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> CULTIST_GAUNTLET = ITEMS.registerItem(
      "cultist_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.CULTIST, ExtendedArmorItem.schoolAttributes(AttributeRegistry.BLOOD_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> CRYOMANCER_GAUNTLET = ITEMS.registerItem(
      "cryomancer_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.CRYOMANCER, ExtendedArmorItem.schoolAttributes(AttributeRegistry.ICE_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> SHADOW_WALKER_GAUNTLET = ITEMS.registerItem(
      "shadow_walker_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.SHADOW_WALKER, ExtendedArmorItem.schoolAttributes(AttributeRegistry.ENDER_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> PRIEST_GAUNTLET = ITEMS.registerItem(
      "priest_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.PRIEST, ExtendedArmorItem.schoolAttributes(AttributeRegistry.HOLY_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> PLAGUED_GAUNTLET = ITEMS.registerItem(
      "plagued_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.PLAGUED, ExtendedArmorItem.schoolAttributes(AttributeRegistry.NATURE_SPELL_POWER)
      )
   );
   public static final DeferredItem<? extends GauntletItem> NETHERITE_BATTLEMAGE_GAUNTLET = ITEMS.registerItem(
      "netherite_battlemage_gauntlet",
      properties -> new ExtraAttributesGauntlet(
         properties, IronsSpellsAndSpellbooksPlugin.NETHERITE_BATTLEMAGE, ExtendedArmorItem.withManaAndSpellPowerAttribute(125, 0.05)
      )
   );

   @SubscribeEvent
   public static void ModifyVanillaCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      ResourceKey<CreativeModeTab> tab = event.getTabKey();
      if (ExpandedCombat.CONFIG.enableGauntlets) {
         if (tab == CreativeModeTabs.COMBAT) {
            event.insertAfter(
               new ItemStack(net.minecraft.world.item.Items.TURTLE_HELMET),
               new ItemStack((ItemLike)WNADERING_MAGICIAN_GAUNTLET.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)WNADERING_MAGICIAN_GAUNTLET.get()),
               new ItemStack((ItemLike)SCARECROW_GAUNTLET.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)SCARECROW_GAUNTLET.get()), new ItemStack((ItemLike)PYROMANCER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)PYROMANCER_GAUNTLET.get()), new ItemStack((ItemLike)ELECTROMANCER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)ELECTROMANCER_GAUNTLET.get()), new ItemStack((ItemLike)ARCHEVOKER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)ARCHEVOKER_GAUNTLET.get()), new ItemStack((ItemLike)CULTIST_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)CULTIST_GAUNTLET.get()), new ItemStack((ItemLike)CRYOMANCER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)CRYOMANCER_GAUNTLET.get()), new ItemStack((ItemLike)SHADOW_WALKER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)SHADOW_WALKER_GAUNTLET.get()), new ItemStack((ItemLike)PRIEST_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)PRIEST_GAUNTLET.get()), new ItemStack((ItemLike)PLAGUED_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)PLAGUED_GAUNTLET.get()),
               new ItemStack((ItemLike)NETHERITE_BATTLEMAGE_GAUNTLET.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
         } else if (tab == ECCreativeTabs.EC_GROUP.getKey()) {
            event.insertAfter(
               new ItemStack((ItemLike)ECItems.BRAWLERS_GAUNTLETS.get()),
               new ItemStack((ItemLike)WNADERING_MAGICIAN_GAUNTLET.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)WNADERING_MAGICIAN_GAUNTLET.get()),
               new ItemStack((ItemLike)SCARECROW_GAUNTLET.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)SCARECROW_GAUNTLET.get()), new ItemStack((ItemLike)PYROMANCER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)PYROMANCER_GAUNTLET.get()), new ItemStack((ItemLike)ELECTROMANCER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)ELECTROMANCER_GAUNTLET.get()), new ItemStack((ItemLike)ARCHEVOKER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)ARCHEVOKER_GAUNTLET.get()), new ItemStack((ItemLike)CULTIST_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)CULTIST_GAUNTLET.get()), new ItemStack((ItemLike)CRYOMANCER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)CRYOMANCER_GAUNTLET.get()), new ItemStack((ItemLike)SHADOW_WALKER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)SHADOW_WALKER_GAUNTLET.get()), new ItemStack((ItemLike)PRIEST_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)PRIEST_GAUNTLET.get()), new ItemStack((ItemLike)PLAGUED_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)PLAGUED_GAUNTLET.get()),
               new ItemStack((ItemLike)NETHERITE_BATTLEMAGE_GAUNTLET.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
         }
      }
   }
}
