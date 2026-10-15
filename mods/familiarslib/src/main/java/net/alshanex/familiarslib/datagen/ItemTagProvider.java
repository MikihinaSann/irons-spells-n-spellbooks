package net.alshanex.familiarslib.datagen;

import io.redspace.ironsspellbooks.util.ModTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ItemTagProvider extends ItemTagsProvider {
   public static final TagKey<Item> SOUND_FOCUS = TagKey.create(Registries.ITEM, new ResourceLocation("familiarslib", "sound_focus"));

   public ItemTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
      super(output, lookupProvider, blockTags);
   }

   protected void addTags(Provider provider) {
      this.tag(SOUND_FOCUS).add(Items.NOTE_BLOCK);
      this.tag(ModTags.SCHOOL_FOCUS).add(Items.NOTE_BLOCK);
   }
}
