package net.neoforged.neoforge.common.brewing;

import net.minecraft.world.item.ItemStack;

/**
 * NeoForge's {@code IBrewingRecipe} — a brewing recipe operating on full
 * ItemStacks rather than potion types.
 */
public interface IBrewingRecipe {
    boolean isInput(ItemStack input);

    boolean isIngredient(ItemStack ingredient);

    ItemStack getOutput(ItemStack input, ItemStack ingredient);
}
