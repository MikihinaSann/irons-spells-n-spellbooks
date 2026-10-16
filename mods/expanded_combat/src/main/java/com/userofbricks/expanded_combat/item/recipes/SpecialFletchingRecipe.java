package com.userofbricks.expanded_combat.item.recipes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public abstract class SpecialFletchingRecipe implements IFletchingRecipe {
   public boolean isSpecial() {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(@NotNull Provider registryAccess) {
      return ItemStack.EMPTY;
   }

   @NotNull
   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.FLETCHING_TABLE);
   }
}
