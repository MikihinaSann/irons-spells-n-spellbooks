package com.somake.somakespells.registries;

import com.somake.somakespells.recipe.pedestal_ritual.PedestalRitualRecipe;
import com.somake.somakespells.recipe.upgrade_forge.UpgradeForgeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
   private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "somakespells");
   private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "somakespells");
   public static final DeferredHolder<RecipeType<?>, RecipeType<UpgradeForgeRecipe>> UPGRADE_FORGE_TYPE = RECIPE_TYPES.register(
      "upgrade_forge", registry -> new RecipeType<UpgradeForgeRecipe>() {
         @Override
         public String toString() {
            return registry.toString();
         }
      }
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeForgeRecipe>> UPGRADE_FORGE_SERIALIZER = RECIPE_SERIALIZERS.register(
      "upgrade_forge", UpgradeForgeRecipe.Serializer::new
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<PedestalRitualRecipe>> PEDESTAL_RITUAL_TYPE = RECIPE_TYPES.register(
      "pedestal_ritual", registry -> new RecipeType<PedestalRitualRecipe>() {
         @Override
         public String toString() {
            return registry.toString();
         }
      }
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PedestalRitualRecipe>> PEDESTAL_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(
      "pedestal_ritual", PedestalRitualRecipe.Serializer::new
   );

   public static void register(IEventBus eventBus) {
      RECIPE_TYPES.register(eventBus);
      RECIPE_SERIALIZERS.register(eventBus);
   }
}
