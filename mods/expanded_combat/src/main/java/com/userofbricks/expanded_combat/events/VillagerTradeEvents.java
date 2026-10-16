package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.init.ECBasePlugin;
import com.userofbricks.expanded_combat.init.ECItems;
import com.userofbricks.expanded_combat.item.ECWeaponItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(
   modid = "expanded_combat"
)
public class VillagerTradeEvents {
   @SubscribeEvent
   public static void villagerTrades(VillagerTradesEvent event) {
      if (event.getType() == VillagerProfession.WEAPONSMITH) {
         Int2ObjectMap<List<ItemListing>> trades = event.getTrades();
         ECItems.IRON_WEAPONS
            .forEach(
               weaponRegistryEntry -> {
                  float dmg = (float)((ECWeaponItem)weaponRegistryEntry.get()).getDamage();
                  boolean block = ((ECWeaponItem)weaponRegistryEntry.get()).weapon.id().equals(ECBasePlugin.MACE.id())
                     || ((ECWeaponItem)weaponRegistryEntry.get()).weapon.id().equals(ECBasePlugin.FLAIL.id())
                     || ((ECWeaponItem)weaponRegistryEntry.get()).weapon.id().equals(ECBasePlugin.GREAT_HAMMER.id());
                  ((List<ItemListing>)trades.get(2))
                     .add(
                        (pTrader, pRandom) -> {
                           int i = 5 + pRandom.nextInt(15);
                           return new MerchantOffer(
                              new ItemCost(Items.EMERALD, Math.min(Math.round(dmg + (block ? 2 : 0)) + i, 64)),
                              EnchantmentHelper.enchantItem(
                                 pRandom, new ItemStack((ItemLike)weaponRegistryEntry.get()), i, pTrader.level().registryAccess(), Optional.empty()
                              ),
                              3,
                              4,
                              0.05F
                           );
                        }
                     );
                  ((List<ItemListing>)trades.get(2))
                     .add(
                        (pTrader, pRandom) -> {
                           int i = 1 + pRandom.nextInt(15);
                           return new MerchantOffer(
                              new ItemCost(Items.EMERALD, Math.min(Math.round(dmg + (block ? 2 : 0)) + i, 64)),
                              new ItemStack((ItemLike)weaponRegistryEntry.get()),
                              3,
                              2,
                              0.05F
                           );
                        }
                     );
               }
            );
         ECItems.DIAMOND_WEAPONS
            .forEach(
               weaponRegistryEntry -> {
                  float dmg = (float)((ECWeaponItem)weaponRegistryEntry.get()).getDamage();
                  boolean block = ((ECWeaponItem)weaponRegistryEntry.get()).weapon.id().equals(ECBasePlugin.MACE.id())
                     || ((ECWeaponItem)weaponRegistryEntry.get()).weapon.id().equals(ECBasePlugin.FLAIL.id())
                     || ((ECWeaponItem)weaponRegistryEntry.get()).weapon.id().equals(ECBasePlugin.GREAT_HAMMER.id());
                  ((List<ItemListing>)trades.get(4))
                     .add(
                        (pTrader, pRandom) -> {
                           int i = 12 + pRandom.nextInt(15);
                           return new MerchantOffer(
                              new ItemCost(Items.EMERALD, Math.min(Math.round(dmg + (block ? 12 : 4)) + i, 64)),
                              EnchantmentHelper.enchantItem(
                                 pRandom, new ItemStack((ItemLike)weaponRegistryEntry.get()), i, pTrader.level().registryAccess(), Optional.empty()
                              ),
                              3,
                              30,
                              0.05F
                           );
                        }
                     );
                  ((List<ItemListing>)trades.get(5))
                     .add(
                        (pTrader, pRandom) -> {
                           int i = 8 + pRandom.nextInt(15);
                           return new MerchantOffer(
                              new ItemCost(Items.EMERALD, Math.min(Math.round(dmg + (block ? 12 : 0)) + i, 64)),
                              new ItemStack((ItemLike)weaponRegistryEntry.get()),
                              3,
                              15,
                              0.05F
                           );
                        }
                     );
               }
            );
      }
   }
}
