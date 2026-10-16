package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.material.PlacementInShield;
import com.userofbricks.expanded_combat.data_components.ShieldMaterials;
import com.userofbricks.expanded_combat.item.ECArrowItem;
import com.userofbricks.expanded_combat.item.ECTippedArrowItem;
import com.userofbricks.expanded_combat.item.ECWeaponItem;
import com.userofbricks.expanded_combat.item.GauntletItem;
import com.userofbricks.expanded_combat.item.QuiverItem;
import java.util.List;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.MOD
)
public class ECCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, "expanded_combat");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EC_GROUP = CREATIVE_TABS.register(
      "expanded_combat",
      () -> CreativeModeTab.builder(net.minecraft.world.item.CreativeModeTab.Row.TOP, 0)
         .icon(() -> new ItemStack(getIcon()))
         .displayItems(
            (displayParameters, output) -> {
               output.accept(ECItems.LEATHER_STICK);
               output.accept(ECItems.GOLD_STICK);
               output.accept(ECItems.IRON_STICK);
               output.accept(ECItems.FLETCHED_STICKS);
               output.accept(ECItems.GAS_BOTTLE);
               output.accept(ECItems.PURIFIED_GAS_BOTTLE);
               output.accept(ECItems.SOLIDIFIED_PURIFICATION);
               output.accept(ECItems.ALLAY_ITEM);
               List<? extends DeferredItem<? extends Item>> items = ECItems.ITEMS
                  .getEntries()
                  .stream()
                  .map(itemDeferredHolder -> (DeferredItem<? extends Item>)itemDeferredHolder)
                  .toList();
               if (ExpandedCombat.CONFIG.enableGauntlets) {
                  for (DeferredItem<? extends Item> deferredItem : items.stream().filter(deferredItemx -> deferredItemx.get() instanceof GauntletItem).toList()) {
                     output.accept(deferredItem);
                  }
               }

               if (ExpandedCombat.CONFIG.enableShields) {
                  for (Material material : PluginInit.materials
                     .values()
                     .stream()
                     .filter(materialReference -> materialReference.defense().placementInShield() != PlacementInShield.NONE)
                     .toList()) {
                     ItemStack stack;
                     if (!material.defense().fireResistant()) {
                        stack = new ItemStack((ItemLike)ECItems.SHIELD.get());
                     } else {
                        stack = new ItemStack((ItemLike)ECItems.SHIELD_FIRE_RESISTANT.get());
                     }

                     stack.set(
                        ItemDataComponents.SHIELD_MATERIALS,
                        new ShieldMaterials(
                           material,
                           material,
                           material,
                           material,
                           material.defense().placementInShield() == PlacementInShield.ALL ? material : ECBasePlugin.IRON,
                           0
                        )
                     );
                     output.accept(stack);
                  }
               }

               if (ExpandedCombat.CONFIG.enableBows) {
                  for (DeferredItem<? extends Item> deferredItem : items.stream().filter(deferredItemx -> deferredItemx.get() instanceof BowItem).toList()) {
                     output.accept(deferredItem);
                  }
               }

               if (ExpandedCombat.CONFIG.enableCrossbows) {
                  for (DeferredItem<? extends Item> deferredItem : items.stream().filter(deferredItemx -> deferredItemx.get() instanceof CrossbowItem).toList()) {
                     output.accept(deferredItem);
                  }
               }

               if (ExpandedCombat.CONFIG.enableQuivers) {
                  for (DeferredItem<? extends Item> deferredItem : items.stream().filter(deferredItemx -> deferredItemx.get() instanceof QuiverItem).toList()) {
                     output.accept(deferredItem);
                  }
               }

               if (ExpandedCombat.CONFIG.enableArrows) {
                  for (DeferredItem<? extends Item> deferredItem : items.stream()
                     .filter(deferredItemx -> deferredItemx.get() instanceof ECArrowItem && !(deferredItemx.get() instanceof ECTippedArrowItem))
                     .toList()) {
                     output.accept(deferredItem);
                  }

                  for (Reference<Potion> potion : BuiltInRegistries.POTION.holders().toList()) {
                     for (DeferredItem<? extends Item> deferredItem : items.stream()
                        .filter(deferredItemx -> deferredItemx.get() instanceof ECTippedArrowItem)
                        .toList()) {
                        ItemStack stack = new ItemStack((ItemLike)deferredItem.get());
                        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                        output.accept(stack);
                     }
                  }
               }

               if (ExpandedCombat.CONFIG.enableWeapons) {
                  for (DeferredItem<? extends Item> deferredItem : items.stream().filter(deferredItemx -> deferredItemx.get() instanceof ECWeaponItem).toList()) {
                     output.accept(deferredItem);
                  }
               }
            }
         )
         .build()
   );

   private static Item getIcon() {
      return (Item)ECItems.DIAMOND_GAUNTLET.get();
   }

   @SubscribeEvent
   public static void ModifyVanillaCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      ResourceKey<CreativeModeTab> tab = event.getTabKey();
      if (tab == CreativeModeTabs.COMBAT) {
         if (ExpandedCombat.CONFIG.enableGauntlets) {
            event.insertBefore(
               new ItemStack(Items.LEATHER_HELMET), new ItemStack((ItemLike)ECItems.LEATHER_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(new ItemStack(Items.IRON_HELMET), new ItemStack((ItemLike)ECItems.IRON_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(new ItemStack(Items.GOLDEN_HELMET), new ItemStack((ItemLike)ECItems.GOLD_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertBefore(
               new ItemStack(Items.DIAMOND_HELMET), new ItemStack((ItemLike)ECItems.DIAMOND_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(
               new ItemStack(Items.NETHERITE_HELMET), new ItemStack((ItemLike)ECItems.NETHERITE_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(new ItemStack(Items.TURTLE_HELMET), new ItemStack((ItemLike)ECItems.SOUL_GAUNTLET.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(
               new ItemStack((ItemLike)ECItems.SOUL_GAUNTLET.get()),
               new ItemStack((ItemLike)ECItems.FIGHTERS_GAUNTLETS.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)ECItems.FIGHTERS_GAUNTLETS.get()),
               new ItemStack((ItemLike)ECItems.BERSERK_GAUNTLETS.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
               new ItemStack((ItemLike)ECItems.BERSERK_GAUNTLETS.get()),
               new ItemStack((ItemLike)ECItems.BRAWLERS_GAUNTLETS.get()),
               TabVisibility.PARENT_AND_SEARCH_TABS
            );
         }

         if (ExpandedCombat.CONFIG.enableShields) {
            for (Material material : PluginInit.materials
               .values()
               .stream()
               .filter(materialReference -> materialReference.defense().placementInShield() != PlacementInShield.NONE)
               .toList()) {
               ItemStack stack;
               if (!material.defense().fireResistant()) {
                  stack = new ItemStack((ItemLike)ECItems.SHIELD.get());
               } else {
                  stack = new ItemStack((ItemLike)ECItems.SHIELD_FIRE_RESISTANT.get());
               }

               stack.set(
                  ItemDataComponents.SHIELD_MATERIALS,
                  new ShieldMaterials(
                     material, material, material, material, material.defense().placementInShield() == PlacementInShield.ALL ? material : ECBasePlugin.IRON, 0
                  )
               );
               event.insertAfter(new ItemStack(Items.SHIELD), stack, TabVisibility.PARENT_AND_SEARCH_TABS);
            }
         }

         List<? extends DeferredItem<? extends Item>> itemList = ECItems.ITEMS
            .getEntries()
            .stream()
            .map(itemDeferredHolder -> (DeferredItem<? extends Item>)itemDeferredHolder)
            .toList();
         if (ExpandedCombat.CONFIG.enableBows) {
            for (DeferredItem<? extends Item> deferredItem : itemList.stream().filter(deferredItemx -> deferredItemx.get() instanceof BowItem).toList()) {
               event.insertAfter(new ItemStack(Items.BOW), new ItemStack((ItemLike)deferredItem.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            }
         }

         if (ExpandedCombat.CONFIG.enableCrossbows) {
            for (DeferredItem<? extends Item> deferredItem : itemList.stream().filter(deferredItemx -> deferredItemx.get() instanceof CrossbowItem).toList()) {
               event.insertAfter(new ItemStack(Items.CROSSBOW), new ItemStack((ItemLike)deferredItem.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            }
         }

         if (ExpandedCombat.CONFIG.enableQuivers) {
            for (DeferredItem<? extends Item> deferredItem : itemList.stream().filter(deferredItemx -> deferredItemx.get() instanceof QuiverItem).toList()) {
               event.insertBefore(new ItemStack(Items.ARROW), new ItemStack((ItemLike)deferredItem.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            }
         }

         if (ExpandedCombat.CONFIG.enableArrows) {
            for (DeferredItem<? extends Item> deferredItem : itemList.stream()
               .filter(deferredItemx -> deferredItemx.get() instanceof ECArrowItem && !(deferredItemx.get() instanceof ECTippedArrowItem))
               .toList()) {
               event.insertAfter(new ItemStack(Items.ARROW), new ItemStack((ItemLike)deferredItem.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            }

            for (Reference<Potion> potion : BuiltInRegistries.POTION.holders().toList()) {
               ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW);
               tippedArrow.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

               for (DeferredItem<? extends Item> deferredItem : itemList.stream()
                  .filter(deferredItemx -> deferredItemx.get() instanceof ECTippedArrowItem)
                  .toList()) {
                  ItemStack stack = new ItemStack((ItemLike)deferredItem.get());
                  stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                  event.insertAfter(tippedArrow, stack, TabVisibility.PARENT_AND_SEARCH_TABS);
               }
            }
         }

         if (ExpandedCombat.CONFIG.enableWeapons) {
            for (DeferredItem<? extends Item> deferredItem : itemList.stream().filter(deferredItemx -> deferredItemx.get() instanceof ECWeaponItem).toList()) {
               event.accept(new ItemStack((ItemLike)deferredItem.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
            }
         }
      } else if (tab == CreativeModeTabs.INGREDIENTS) {
         event.insertAfter(new ItemStack(Items.STICK), new ItemStack((ItemLike)ECItems.LEATHER_STICK.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(
            new ItemStack((ItemLike)ECItems.LEATHER_STICK.get()), new ItemStack((ItemLike)ECItems.GOLD_STICK.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(
            new ItemStack((ItemLike)ECItems.GOLD_STICK.get()), new ItemStack((ItemLike)ECItems.IRON_STICK.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(
            new ItemStack((ItemLike)ECItems.IRON_STICK.get()), new ItemStack((ItemLike)ECItems.FLETCHED_STICKS.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack((ItemLike)ECItems.ALLAY_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack((ItemLike)ECItems.BAD_SOUL.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack((ItemLike)ECItems.GOOD_SOUL.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.insertAfter(
            new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack((ItemLike)ECItems.SOLIDIFIED_PURIFICATION.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(
            new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack((ItemLike)ECItems.PURIFIED_GAS_BOTTLE.get()), TabVisibility.PARENT_AND_SEARCH_TABS
         );
         event.insertAfter(new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack((ItemLike)ECItems.GAS_BOTTLE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }
}
