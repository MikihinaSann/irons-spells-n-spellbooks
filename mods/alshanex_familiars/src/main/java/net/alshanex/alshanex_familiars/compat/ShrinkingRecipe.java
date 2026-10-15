package net.alshanex.alshanex_familiars.compat;

import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class ShrinkingRecipe implements Recipe<ShrinkingRecipe.Input> {
   public abstract Ingredient getInputIngredient();

   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeRegistry.SHRINKING_RECIPE_TYPE.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public record Input(ItemStack item) implements RecipeInput {
      public ItemStack getItem(int index) {
         return this.item;
      }

      public int size() {
         return 1;
      }
   }
}
