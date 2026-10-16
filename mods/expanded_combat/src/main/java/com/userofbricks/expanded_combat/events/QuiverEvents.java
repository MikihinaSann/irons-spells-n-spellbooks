package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.item.QuiverItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class QuiverEvents {
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onArrowItemPickup(Pre evt) {
      if (ExpandedCombat.CONFIG.enableQuivers) {
         Player player = evt.getPlayer();
         ItemStack toPickup = evt.getItemEntity().getItem();
         SlotResult slotResult = CuriosApi.getCuriosInventory(player)
            .flatMap(curiosInventory -> curiosInventory.findFirstCurio(item -> item.getItem() instanceof QuiverItem))
            .orElse(null);
         if (toPickup.is(ItemTags.ARROWS) && slotResult != null && slotResult.stack().getItem() instanceof QuiverItem quiverItem) {
            BundleContents bundlecontents = (BundleContents)slotResult.stack().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            QuiverItem.MutableQuiverContents bundlecontents$mutable = new QuiverItem.MutableQuiverContents(
               bundlecontents, quiverItem.getMaterial().offense().quiverStacks()
            );
            int b = toPickup.getCount();
            int i = bundlecontents$mutable.tryInsert(toPickup);
            if (i > 0) {
               slotResult.stack().set(DataComponents.BUNDLE_CONTENTS, bundlecontents$mutable.toImmutable());
               quiverItem.playInsert(player);
               player.awardStat(Stats.ITEM_PICKED_UP.get(toPickup.getItem()), i);
               toPickup.setCount(b - i);
            }
         }
      }
   }
}
