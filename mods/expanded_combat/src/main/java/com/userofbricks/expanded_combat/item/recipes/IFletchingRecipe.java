package com.userofbricks.expanded_combat.item.recipes;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.init.ECRecipeSerializerInit;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IFletchingRecipe extends Recipe<FletchingRecipeInput> {
   ResourceLocation FLETCHING_RECIPE_ID = ExpandedCombat.modLoc("ec_fletching");

   @Nonnull
   default RecipeType<?> getType() {
      return (RecipeType<?>)ECRecipeSerializerInit.FLETCHING_TYPE.get();
   }

   default boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   Ingredient getBase();

   Ingredient getAddition();

   int getMaxCraftingAmount();
}
