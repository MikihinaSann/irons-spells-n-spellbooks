package com.userofbricks.ecisasbplugin.util;

import com.userofbricks.ecisasbplugin.config.ECISaSbConfig;
import com.userofbricks.ecisasbplugin.item.ECISaSbItems;
import com.userofbricks.expanded_combat.config.ECConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class LangStrings extends com.userofbricks.expanded_combat.datagen.LangStrings {
   private static final Supplier<String> configLangStartGetter = () -> "text.autoconfig." + ((Config)ECISaSbConfig.class.getAnnotation(Config.class)).name();

   public LangStrings(PackOutput output) {
      super(output, "expanded_combat");
   }

   protected void addTranslations() {
      for (DeferredHolder<Item, ? extends Item> deferredItem : ECISaSbItems.ITEMS.getEntries()) {
         String locationName = deferredItem.getId().getPath();
         String name = locationToName(locationName);
         this.addItem(deferredItem, name);
      }

      List<String> alreadyAddedStrings = new ArrayList<>();
      String configLangStart = configLangStartGetter.get();
      this.add(configLangStart + ".title", "EC Iron's Spells & Spellbooks Settings");
      Arrays.stream(ECConfig.class.getDeclaredFields())
         .collect(
            Collectors.groupingBy(
               field -> this.getOrCreateCategoryForField(field, alreadyAddedStrings, configLangStart), LinkedHashMap::new, Collectors.toList()
            )
         )
         .forEach((key, value) -> value.forEach(field -> this.ifNotExcludedRegisterLangs(field, configLangStart, alreadyAddedStrings)));
   }
}
