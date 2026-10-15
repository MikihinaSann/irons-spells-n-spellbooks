package com.userofbricks.ecisasbplugin.datagen;

import com.userofbricks.ecisasbplugin.datagen.models.ECISasbItemModelProvider;
import com.userofbricks.ecisasbplugin.datagen.recipes.ECISaSbRecipeProvider;
import com.userofbricks.ecisasbplugin.datagen.tags.ECISaSbBlockTagsProvider;
import com.userofbricks.ecisasbplugin.datagen.tags.ECISaSbItemTagsProvider;
import com.userofbricks.ecisasbplugin.util.LangStrings;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(
   modid = "ec_isasb_plugin",
   bus = Bus.MOD
)
public class DataGenerators {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = event.getGenerator().getPackOutput();
      CompletableFuture<Provider> provider = event.getLookupProvider();
      ExistingFileHelper helper = event.getExistingFileHelper();
      generator.addProvider(event.includeClient(), new LangStrings(output));
      generator.addProvider(event.includeClient(), new ECISasbItemModelProvider(output, helper));
      generator.addProvider(event.includeServer(), new ECISaSbRecipeProvider(output, provider));
      ECISaSbBlockTagsProvider blockTagsProvider = new ECISaSbBlockTagsProvider(output, provider, helper);
      generator.addProvider(event.includeServer(), blockTagsProvider);
      generator.addProvider(event.includeServer(), new ECISaSbItemTagsProvider(output, provider, blockTagsProvider.contentsGetter(), helper));
   }
}
