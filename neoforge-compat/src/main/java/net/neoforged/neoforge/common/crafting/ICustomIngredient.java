package net.neoforged.neoforge.common.crafting;

import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public interface ICustomIngredient {
   boolean test(ItemStack var1);

   Stream<ItemStack> getItems();

   boolean isSimple();

   IngredientType<?> getType();

   @NonExtendable
   default Ingredient toVanilla() {
      // NeoForge's patched Ingredient ctor carries the custom ingredient; on
      // Fabric we build the vanilla value list, then attach the carrier.
      Ingredient ingredient = Ingredient.of(this.getItems().toArray(ItemStack[]::new));
      ((net.neoforged.neoforge.common.extensions.IIngredientExtension) (Object) ingredient)
            .setCustomIngredient(this);
      return ingredient;
   }
}
