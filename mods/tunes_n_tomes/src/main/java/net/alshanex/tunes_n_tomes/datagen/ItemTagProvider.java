package net.alshanex.tunes_n_tomes.datagen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ItemTagProvider extends ItemTagsProvider {
   public static final TagKey<Item> MELODY_FOCUS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "melody_focus"));

   public ItemTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
      super(output, lookupProvider, blockTags);
   }

   protected void addTags(Provider provider) {
   }
}
