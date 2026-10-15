package net.neoforged.neoforge.internal.fabric;

import java.util.List;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

/**
 * Bridges a NeoForge {@link ICustomIngredient} to Fabric's {@link CustomIngredient}
 * so {@code neoforge:*} ingredient types can be used in recipe JSONs through
 * Fabric's {@code "fabric:type"} dispatch.
 */
public final class NeoForgeCustomIngredient implements CustomIngredient {

    private final ICustomIngredient delegate;
    private final CustomIngredientSerializer<?> serializer;

    public NeoForgeCustomIngredient(ICustomIngredient delegate, CustomIngredientSerializer<?> serializer) {
        this.delegate = delegate;
        this.serializer = serializer;
    }

    public ICustomIngredient delegate() {
        return this.delegate;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.delegate.test(stack);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return this.delegate.getItems().toList();
    }

    @Override
    public boolean requiresTesting() {
        return !this.delegate.isSimple();
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return this.serializer;
    }
}
