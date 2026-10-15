package net.alshanex.alshanex_familiars.registry;

import net.alshanex.alshanex_familiars.compat.FixedShrinkingRecipe;
import net.alshanex.alshanex_familiars.compat.FoodShrinkingRecipe;
import net.alshanex.alshanex_familiars.compat.ShrinkingRecipe;
import net.alshanex.alshanex_familiars.recipe_types.RitualRecipeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeRegistry {
   private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "alshanex_familiars");
   private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "alshanex_familiars");
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RitualRecipeType>> RITUAL_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
      "ritual_recipe", RitualRecipeType.Serializer::new
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<RitualRecipeType>> RITUAL_RECIPE_TYPE = RECIPE_TYPES.register(
      "ritual_recipe", registry -> new RecipeType<RitualRecipeType>() {
         @Override
         public String toString() {
            return registry.toString();
         }
      }
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FixedShrinkingRecipe>> FIXED_SHRINKING_SERIALIZER = RECIPE_SERIALIZERS.register(
      "shrinking_recipe", FixedShrinkingRecipe.Serializer::new
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FoodShrinkingRecipe>> FOOD_SHRINKING_SERIALIZER = RECIPE_SERIALIZERS.register(
      "food_shrinking", FoodShrinkingRecipe.Serializer::new
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<ShrinkingRecipe>> SHRINKING_RECIPE_TYPE = RECIPE_TYPES.register(
      "shrinking_recipe", registry -> new RecipeType<ShrinkingRecipe>() {
         @Override
         public String toString() {
            return registry.toString();
         }
      }
   );

   public static void register(IEventBus eventBus) {
      RECIPE_TYPES.register(eventBus);
      RECIPE_SERIALIZERS.register(eventBus);
   }
}
