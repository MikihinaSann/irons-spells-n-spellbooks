package com.userofbricks.ecisasbplugin.datagen.models;

import com.userofbricks.ecisasbplugin.item.ECISaSbItems;
import com.userofbricks.expanded_combat.datagen.models.ECItemModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ECISasbItemModelProvider extends ECItemModelProvider {
   public ECISasbItemModelProvider(PackOutput output, ExistingFileHelper helper) {
      super(output, "ec_isasb_plugin", helper);
   }

   protected void registerModels() {
      for (DeferredHolder<Item, ? extends Item> item : ECISaSbItems.ITEMS.getEntries()) {
         this.dynamicallyGenerateModels(item);
      }
   }
}
