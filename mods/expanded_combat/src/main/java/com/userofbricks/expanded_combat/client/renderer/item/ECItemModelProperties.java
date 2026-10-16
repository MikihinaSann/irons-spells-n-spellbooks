package com.userofbricks.expanded_combat.client.renderer.item;

import com.userofbricks.expanded_combat.init.ECItems;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import com.userofbricks.expanded_combat.item.ArrowBlockWeaponItem;
import com.userofbricks.expanded_combat.item.ECBowItem;
import com.userofbricks.expanded_combat.item.ECCrossBowItem;
import com.userofbricks.expanded_combat.item.ECShieldItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.neoforged.neoforge.registries.DeferredItem;

public class ECItemModelProperties {
   public static void registerModelOverrides() {
      for (DeferredItem<? extends Item> deferredItem : ECItems.ITEMS.getEntries().stream().map(itemDeferredHolder -> (DeferredItem)itemDeferredHolder).toList()) {
         if (deferredItem.get() instanceof ECShieldItem) {
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("blocking"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> livingEntity != null
                     && livingEntity.isUsingItem()
                     && livingEntity.getUseItem() == itemStack
                  ? 1.0F
                  : 0.0F
            );
         } else if (deferredItem.get() instanceof ECBowItem) {
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("pulling"),
               (itemStack, clientWorld, livingEntity, textureLayer) -> livingEntity != null
                     && livingEntity.isUsingItem()
                     && livingEntity.getUseItem() == itemStack
                  ? 1.0F
                  : 0.0F
            );
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("pull"),
               (itemStack, clientWorld, livingEntity, textureLayer) -> {
                  if (livingEntity == null) {
                     return 0.0F;
                  } else {
                     return livingEntity.getUseItem() != itemStack
                        ? 0.0F
                        : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                  }
               }
            );
         } else if (deferredItem.get() instanceof ECCrossBowItem) {
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("pull"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> {
                  if (livingEntity == null) {
                     return 0.0F;
                  } else {
                     return CrossbowItem.isCharged(itemStack)
                        ? 0.0F
                        : (float)(itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks())
                           / CrossbowItem.getChargeDuration(itemStack, livingEntity);
                  }
               }
            );
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("pulling"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> livingEntity != null
                     && livingEntity.isUsingItem()
                     && livingEntity.getUseItem() == itemStack
                     && !CrossbowItem.isCharged(itemStack)
                  ? 1.0F
                  : 0.0F
            );
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("charged"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F
            );
            ItemProperties.register((Item)deferredItem.get(), ResourceLocation.parse("firework"), (itemStack, clientLevel, livingEntity, textureLayer) -> {
               ChargedProjectiles chargedprojectiles = (ChargedProjectiles)itemStack.get(DataComponents.CHARGED_PROJECTILES);
               return chargedprojectiles != null && chargedprojectiles.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
            });
         } else if (deferredItem.get() instanceof ArrowBlockWeaponItem) {
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("blocking"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> livingEntity != null
                     && livingEntity.isUsingItem()
                     && livingEntity.getUseItem() == itemStack
                  ? 1.0F
                  : 0.0F
            );
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("blocked_recently"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> livingEntity != null && ArrowBlockWeaponItem.blockedRecently(livingEntity) ? 1.0F : 0.0F
            );
            ItemProperties.register(
               (Item)deferredItem.get(),
               ResourceLocation.parse("block_pos"),
               (itemStack, clientLevel, livingEntity, textureLayer) -> livingEntity != null ? ArrowBlockWeaponItem.blockPosition(itemStack).id / 4.0F : 0.0F
            );
         }
      }

      ItemProperties.register((Item)ECItems.HEART_STEALER.get(), ResourceLocation.parse("stage"), (itemStack, clientLevel, livingEntity, textureLayer) -> {
         int charge = (Integer)itemStack.getOrDefault(ItemDataComponents.CHARGE, 0);
         if (charge >= 490) {
            return 1.0F;
         } else if (charge >= 336) {
            return 0.8F;
         } else if (charge >= 173) {
            return 0.6F;
         } else {
            return charge > 10 ? 0.4F : 0.0F;
         }
      });
   }
}
