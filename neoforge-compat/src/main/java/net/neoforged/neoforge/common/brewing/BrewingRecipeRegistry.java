package net.neoforged.neoforge.common.brewing;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NeoForge's {@code BrewingRecipeRegistry} — custom brewing recipes collected
 * through {@code PotionBrewing.Builder#addRecipe}, consulted by the
 * {@code PotionBrewing} mix hooks.
 */
public final class BrewingRecipeRegistry {
    private static final List<IBrewingRecipe> RECIPES = new CopyOnWriteArrayList<>();

    private BrewingRecipeRegistry() {
    }

    public static void addRecipe(IBrewingRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static List<IBrewingRecipe> getRecipes() {
        return RECIPES;
    }

    public static boolean isValidIngredient(ItemStack stack) {
        for (IBrewingRecipe recipe : RECIPES) {
            if (recipe.isIngredient(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidInput(ItemStack stack) {
        for (IBrewingRecipe recipe : RECIPES) {
            if (recipe.isInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (input.isEmpty() || ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }
        for (IBrewingRecipe recipe : RECIPES) {
            ItemStack output = recipe.getOutput(input, ingredient);
            if (!output.isEmpty()) {
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean hasOutput(ItemStack input, ItemStack ingredient) {
        return !getOutput(input, ingredient).isEmpty();
    }
}
