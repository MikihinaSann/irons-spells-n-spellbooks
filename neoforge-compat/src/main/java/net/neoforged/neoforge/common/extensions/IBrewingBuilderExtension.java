package net.neoforged.neoforge.common.extensions;

import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

/**
 * NeoForge's {@code PotionBrewing.Builder#addRecipe(IBrewingRecipe)} — injected
 * onto the vanilla builder via Loom {@code injected_interfaces} (compile) and
 * an api mixin (runtime).
 */
public interface IBrewingBuilderExtension {
    default void addRecipe(IBrewingRecipe recipe) {
        BrewingRecipeRegistry.addRecipe(recipe);
    }
}
