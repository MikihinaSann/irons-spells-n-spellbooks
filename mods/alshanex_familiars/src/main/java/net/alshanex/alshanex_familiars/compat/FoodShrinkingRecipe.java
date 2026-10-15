package net.alshanex.alshanex_familiars.compat;

import com.mojang.serialization.MapCodec;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.alshanex.familiarslib.util.consumables.FamiliarFoodComponent;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FoodShrinkingRecipe extends ShrinkingRecipe {
   public static final FoodShrinkingRecipe INSTANCE = new FoodShrinkingRecipe();

   @Override
   public Ingredient getInputIngredient() {
      return Ingredient.EMPTY;
   }

   public boolean matches(ShrinkingRecipe.Input input, Level level) {
      ItemStack stack = input.item();
      return stack.has(DataComponents.FOOD) && !stack.has(ComponentRegistry.FAMILIAR_FOOD);
   }

   public ItemStack assemble(ShrinkingRecipe.Input input, Provider registries) {
      ItemStack result = input.item().copy();
      result.setCount(1);
      FoodProperties food = (FoodProperties)result.get(DataComponents.FOOD);
      if (food != null) {
         result.set(ComponentRegistry.FAMILIAR_FOOD, new FamiliarFoodComponent(food.nutrition(), food.saturation()));
      }

      return result;
   }

   public ItemStack getResultItem(Provider registries) {
      return ItemStack.EMPTY;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeRegistry.FOOD_SHRINKING_SERIALIZER.get();
   }

   public static class Serializer implements RecipeSerializer<FoodShrinkingRecipe> {
      public static final MapCodec<FoodShrinkingRecipe> CODEC = MapCodec.unit(FoodShrinkingRecipe.INSTANCE);
      public static final StreamCodec<RegistryFriendlyByteBuf, FoodShrinkingRecipe> STREAM_CODEC = StreamCodec.unit(FoodShrinkingRecipe.INSTANCE);

      public MapCodec<FoodShrinkingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, FoodShrinkingRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
