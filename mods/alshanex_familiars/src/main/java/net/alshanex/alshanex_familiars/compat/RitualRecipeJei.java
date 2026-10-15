package net.alshanex.alshanex_familiars.compat;

import java.util.List;
import net.alshanex.alshanex_familiars.recipe_types.RitualRecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record RitualRecipeJei(Ingredient centralItem, List<RitualRecipeType.RitualIngredient> inputItems, ItemStack result) {
}
