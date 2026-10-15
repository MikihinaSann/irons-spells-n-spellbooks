package com.userofbricks.ecisasbplugin.datagen.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class ECISaSbBlockTagsProvider extends BlockTagsProvider {
   public ECISaSbBlockTagsProvider(PackOutput output, CompletableFuture<Provider> provider, ExistingFileHelper helper) {
      super(output, provider, "ec_isasb_plugin", helper);
   }

   protected void addTags(@NotNull Provider pProvider) {
   }
}
