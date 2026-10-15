package com.userofbricks.ecisasbplugin.datagen.tags;

import com.userofbricks.ecisasbplugin.item.ECISaSbItems;
import com.userofbricks.expanded_combat.init.ECBasePlugin;
import com.userofbricks.expanded_combat.init.ECTags;
import com.userofbricks.expanded_combat.item.ArrowBlockWeaponItem;
import com.userofbricks.expanded_combat.item.ECArrowItem;
import com.userofbricks.expanded_combat.item.ECBowItem;
import com.userofbricks.expanded_combat.item.ECCrossBowItem;
import com.userofbricks.expanded_combat.item.ECWeaponItem;
import com.userofbricks.expanded_combat.item.GauntletItem;
import com.userofbricks.expanded_combat.item.PotionWeaponItem;
import com.userofbricks.expanded_combat.item.QuiverItem;
import com.userofbricks.expanded_combat.item.SlamWeaponItem;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class ECISaSbItemTagsProvider extends ItemTagsProvider {
   public ECISaSbItemTagsProvider(
      PackOutput output, CompletableFuture<Provider> provider, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture, ExistingFileHelper helper
   ) {
      super(output, provider, tagLookupCompletableFuture, "ec_isasb_plugin", helper);
   }

   protected void addTags(@NotNull Provider pProvider) {
      for (DeferredHolder<Item, ? extends Item> deferredItem : ECISaSbItems.ITEMS.getEntries()) {
         if (deferredItem.get() instanceof ECArrowItem item) {
            this.tag(ItemTags.ARROWS).add(item);
         }

         if (deferredItem.get() instanceof ECBowItem item) {
            this.tag(Items.TOOLS_BOW).add(item);
         }

         if (deferredItem.get() instanceof ECCrossBowItem item) {
            this.tag(Items.TOOLS_CROSSBOW).add(item);
         }

         if (deferredItem.get() instanceof GauntletItem item) {
            this.tag(ECTags.GAUNTLETS).add(item);
            this.tag(ItemTags.TRIMMABLE_ARMOR).add(item);
            this.tag(ECTags.GAUNTLET_ENCHANTABLE).add(item);
            this.tag(ECTags.AGILITY_ENCHANTABLE).add(item);
         }

         if (deferredItem.get() instanceof QuiverItem item) {
            this.tag(ECTags.QUIVERS).add(item);
         }

         if (deferredItem.get() instanceof PotionWeaponItem item) {
            this.tag(ECTags.POTION_WEAPONS).add(item);
         }

         if (deferredItem.get() instanceof ECWeaponItem item
            && (
               item.weapon.id() == ECBasePlugin.BATTLE_STAFF.id()
                  || item.weapon.id() == ECBasePlugin.BROAD_SWORD.id()
                  || item.weapon.id() == ECBasePlugin.CLAYMORE.id()
                  || item.weapon.id() == ECBasePlugin.DANCERS_SWORD.id()
                  || item.weapon.id() == ECBasePlugin.GLAIVE.id()
            )) {
            this.tag(ItemTags.DYEABLE).add(item);
         }

         if (deferredItem.get() instanceof ArrowBlockWeaponItem item) {
            this.tag(ECTags.BLOCKING_ENCHANTABLE).add(item);
         }

         if (deferredItem.get() instanceof SlamWeaponItem item) {
            this.tag(ECTags.GROUND_SLAM).add(item);
         }
      }
   }
}
