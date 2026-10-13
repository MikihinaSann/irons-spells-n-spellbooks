package net.veroxuniverse.crystal_chronicles.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CCTags {
   public static class Blocks {
      public static final TagKey<Block> INCORRECT_FOR_CRYSTAL_TOOL = createTag("incorrect_for_crystal_tool");
      public static final TagKey<Block> NEEDS_CRYSTAL_TOOL = createTag("needs_crystal_tool");
      public static final TagKey<Block> CC_BLOOD_BLOCK = createTag("cc_blood_block");
      public static final TagKey<Block> CC_ARTREE_BLOCK = createTag("cc_artree_block");

      private static TagKey<Block> createTag(String name) {
         return net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("crystal_chronicles", name));
      }
   }
}
