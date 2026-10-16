package com.userofbricks.expanded_combat.item.recipes;

import com.userofbricks.expanded_combat.init.ECRecipeSerializerInit;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public interface IShieldSmithingRecipe extends Recipe<ShieldSmithingRecipeInput> {
   @Nonnull
   default RecipeType<?> getType() {
      return (RecipeType<?>)ECRecipeSerializerInit.SHIELD_TYPE.get();
   }

   default boolean canCraftInDimensions(int width, int height) {
      return width >= 6 && height >= 1;
   }

   @NotNull
   default ItemStack getToastSymbol() {
      return new ItemStack(Blocks.SMITHING_TABLE);
   }

   Ingredient getBase();

   Ingredient getURAddition();

   Ingredient getULAddition();

   Ingredient getMAddition();

   Ingredient getDRAddition();

   Ingredient getDLAddition();
}
