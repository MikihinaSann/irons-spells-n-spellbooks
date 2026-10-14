package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge's extension methods patched onto {@link Ingredient} - custom
 * ingredient support. Implemented by the Ingredient mixin.
 */
public interface IIngredientExtension {

    default boolean isCustom() {
        return false;
    }

    @Nullable
    default ICustomIngredient getCustomIngredient() {
        return null;
    }

    default void setCustomIngredient(@Nullable ICustomIngredient customIngredient) {
    }

    default Ingredient.Value[] getValues() {
        return new Ingredient.Value[0];
    }

    /** True when this ingredient is a plain vanilla item/tag ingredient. */
    default boolean isSimple() {
        return true;
    }

    /**
     * NeoForge name for vanilla {@code Ingredient#isEmpty()}.
     */
    default boolean hasNoItems() {
        return ((net.minecraft.world.item.crafting.Ingredient) (Object) this).isEmpty();
    }
}
