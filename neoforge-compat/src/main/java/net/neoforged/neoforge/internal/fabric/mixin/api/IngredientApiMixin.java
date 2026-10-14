package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge: {@code net.minecraft.world.item.crafting.Ingredient} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.world.item.crafting.Ingredient.class)
public abstract class IngredientApiMixin implements net.neoforged.neoforge.common.extensions.IIngredientExtension {
}
