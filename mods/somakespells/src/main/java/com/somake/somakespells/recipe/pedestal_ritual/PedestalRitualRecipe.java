package com.somake.somakespells.recipe.pedestal_ritual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModRecipes;
import com.somake.somakespells.ritual.RitualElementalMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record PedestalRitualRecipe(
   Ingredient centerItem,
   List<PedestalRitualRecipe.PedestalRequirement> pedestalGroups,
   ItemStack result,
   boolean activatesSoulFire,
   boolean activatesInfernalFire,
   RitualElementalMode requiredMode
) implements Recipe<PedestalRitualRecipe.Input> {
   private static final Codec<List<PedestalRitualRecipe.PedestalRequirement>> PEDESTAL_GROUPS_CODEC = PedestalRitualRecipe.PedestalRequirement.CODEC
      .codec()
      .listOf()
      .flatXmap(
         list -> list.isEmpty()
            ? DataResult.error(() -> "Pedestal rituals must define at least one pedestal group")
            : (list.size() > 24 ? DataResult.error(() -> "Pedestal rituals support at most 24 pedestal groups inside the 5x5 area") : DataResult.success(list)),
         DataResult::success
      );
   private static final Codec<RitualElementalMode> RITUAL_MODE_CODEC = Codec.STRING
      .flatXmap(id -> DataResult.success(RitualElementalMode.fromId(id)), mode -> DataResult.success(mode.id()));

   public boolean matchesCenter(ItemStack stack) {
      return this.centerItem.test(stack) && stack.getCount() == 1;
   }

   public boolean matches(PedestalRitualRecipe.Input input, Level level) {
      return this.matchesCenter(input.centerItem()) && this.findMatchingPedestalIndexes(input.pedestals()).isPresent();
   }

   public Optional<List<Integer>> findMatchingPedestalIndexes(List<NonNullList<ItemStack>> pedestals) {
      if (pedestals.size() < this.pedestalGroups.size()) {
         return Optional.empty();
      } else {
         boolean[] usedPedestals = new boolean[pedestals.size()];
         List<Integer> matchedIndexes = new ArrayList<>();
         return this.matchGroups(0, pedestals, usedPedestals, matchedIndexes) ? Optional.of(List.copyOf(matchedIndexes)) : Optional.empty();
      }
   }

   private boolean matchGroups(int groupIndex, List<NonNullList<ItemStack>> pedestals, boolean[] usedPedestals, List<Integer> matchedIndexes) {
      if (groupIndex >= this.pedestalGroups.size()) {
         return true;
      } else {
         PedestalRitualRecipe.PedestalRequirement group = this.pedestalGroups.get(groupIndex);

         for (int pedestalIndex = 0; pedestalIndex < pedestals.size(); pedestalIndex++) {
            if (!usedPedestals[pedestalIndex] && group.test(pedestals.get(pedestalIndex))) {
               usedPedestals[pedestalIndex] = true;
               matchedIndexes.add(pedestalIndex);
               if (this.matchGroups(groupIndex + 1, pedestals, usedPedestals, matchedIndexes)) {
                  return true;
               }

               matchedIndexes.remove(matchedIndexes.size() - 1);
               usedPedestals[pedestalIndex] = false;
            }
         }

         return false;
      }
   }

   public ItemStack assemble(PedestalRitualRecipe.Input input, Provider registries) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return false;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.PEDESTAL_RITUAL_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipes.PEDESTAL_RITUAL_TYPE.get();
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean activatesElementalMode() {
      return this.activatesSoulFire || this.activatesInfernalFire;
   }

   public record CountedIngredient(Ingredient ingredient, int count) {
      private static final Codec<Integer> COUNT_CODEC = Codec.INT
         .flatXmap(
            value -> value >= 1 && value <= 16 ? DataResult.success(value) : DataResult.error(() -> "Pedestal ritual item counts must be between 1 and 16"),
            DataResult::success
         );
      public static final MapCodec<PedestalRitualRecipe.CountedIngredient> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Ingredient.CODEC.fieldOf("ingredient").forGetter(PedestalRitualRecipe.CountedIngredient::ingredient),
               COUNT_CODEC.fieldOf("count").forGetter(PedestalRitualRecipe.CountedIngredient::count)
            )
            .apply(builder, PedestalRitualRecipe.CountedIngredient::new)
      );

      public boolean test(NonNullList<ItemStack> stacks) {
         int matchedCount = 0;

         for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
               if (!this.ingredient.test(stack)) {
                  return false;
               }

               matchedCount += stack.getCount();
            }
         }

         return matchedCount == this.count;
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, PedestalRitualRecipe.CountedIngredient ingredient) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient.ingredient());
         buffer.writeVarInt(ingredient.count());
      }

      private static PedestalRitualRecipe.CountedIngredient fromNetwork(RegistryFriendlyByteBuf buffer) {
         return new PedestalRitualRecipe.CountedIngredient((Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readVarInt());
      }
   }

   public record Input(ItemStack centerItem, List<NonNullList<ItemStack>> pedestals) implements RecipeInput {
      public ItemStack getItem(int index) {
         return index == 0 ? this.centerItem : ItemStack.EMPTY;
      }

      public int size() {
         return 1;
      }
   }

   public record PedestalRequirement(List<PedestalRitualRecipe.CountedIngredient> ingredients) {
      private static final Codec<List<PedestalRitualRecipe.CountedIngredient>> INGREDIENT_LIST_CODEC = PedestalRitualRecipe.CountedIngredient.CODEC
         .codec()
         .listOf()
         .flatXmap(
            list -> list.isEmpty() ? DataResult.error(() -> "Each pedestal must define at least one ingredient") : DataResult.success(list),
            DataResult::success
         );
      public static final MapCodec<PedestalRitualRecipe.PedestalRequirement> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(INGREDIENT_LIST_CODEC.fieldOf("ingredients").forGetter(PedestalRitualRecipe.PedestalRequirement::ingredients))
            .apply(builder, PedestalRitualRecipe.PedestalRequirement::new)
      );

      public boolean test(NonNullList<ItemStack> stacks) {
         List<ItemStack> remainingStacks = new ArrayList<>();

         for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
               remainingStacks.add(stack.copy());
            }
         }

         for (PedestalRitualRecipe.CountedIngredient countedIngredient : this.ingredients) {
            int remaining = countedIngredient.count();

            for (ItemStack stackx : remainingStacks) {
               if (!stackx.isEmpty() && countedIngredient.ingredient().test(stackx)) {
                  int remove = Math.min(remaining, stackx.getCount());
                  stackx.shrink(remove);
                  remaining -= remove;
                  if (remaining == 0) {
                     break;
                  }
               }
            }

            if (remaining > 0) {
               return false;
            }
         }

         return remainingStacks.stream().allMatch(ItemStack::isEmpty);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, PedestalRitualRecipe.PedestalRequirement requirement) {
         buffer.writeVarInt(requirement.ingredients().size());

         for (PedestalRitualRecipe.CountedIngredient ingredient : requirement.ingredients()) {
            PedestalRitualRecipe.CountedIngredient.toNetwork(buffer, ingredient);
         }
      }

      private static PedestalRitualRecipe.PedestalRequirement fromNetwork(RegistryFriendlyByteBuf buffer) {
         int size = buffer.readVarInt();
         List<PedestalRitualRecipe.CountedIngredient> ingredients = new ArrayList<>();

         for (int i = 0; i < size; i++) {
            ingredients.add(PedestalRitualRecipe.CountedIngredient.fromNetwork(buffer));
         }

         return new PedestalRitualRecipe.PedestalRequirement(ingredients);
      }
   }

   public static class Serializer implements RecipeSerializer<PedestalRitualRecipe> {
      public static final MapCodec<PedestalRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Ingredient.CODEC.fieldOf("center_item").forGetter(PedestalRitualRecipe::centerItem),
               PedestalRitualRecipe.PEDESTAL_GROUPS_CODEC.fieldOf("pedestal_groups").forGetter(PedestalRitualRecipe::pedestalGroups),
               ItemStack.SINGLE_ITEM_CODEC.fieldOf("result").forGetter(PedestalRitualRecipe::result),
               Codec.BOOL.optionalFieldOf("activate_soul_fire", false).forGetter(PedestalRitualRecipe::activatesSoulFire),
               Codec.BOOL.optionalFieldOf("activate_infernal_fire", false).forGetter(PedestalRitualRecipe::activatesInfernalFire),
               PedestalRitualRecipe.RITUAL_MODE_CODEC
                  .optionalFieldOf("required_ritual_mode", RitualElementalMode.NORMAL)
                  .forGetter(PedestalRitualRecipe::requiredMode)
            )
            .apply(builder, PedestalRitualRecipe.Serializer::createFromJson)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, PedestalRitualRecipe> STREAM_CODEC = StreamCodec.of(
         PedestalRitualRecipe.Serializer::toNetwork, PedestalRitualRecipe.Serializer::fromNetwork
      );

      private static void toNetwork(RegistryFriendlyByteBuf buffer, PedestalRitualRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.centerItem());
         buffer.writeVarInt(recipe.pedestalGroups().size());

         for (PedestalRitualRecipe.PedestalRequirement requirement : recipe.pedestalGroups()) {
            PedestalRitualRecipe.PedestalRequirement.toNetwork(buffer, requirement);
         }

         ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
         buffer.writeBoolean(recipe.activatesSoulFire());
         buffer.writeBoolean(recipe.activatesInfernalFire());
         buffer.writeUtf(recipe.requiredMode().id());
      }

      private static PedestalRitualRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient centerItem = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         int groupCount = buffer.readVarInt();
         List<PedestalRitualRecipe.PedestalRequirement> pedestalGroups = new ArrayList<>();

         for (int i = 0; i < groupCount; i++) {
            pedestalGroups.add(PedestalRitualRecipe.PedestalRequirement.fromNetwork(buffer));
         }

         ItemStack result = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         boolean activatesSoulFire = buffer.readBoolean();
         boolean activatesInfernalFire = buffer.readBoolean();
         RitualElementalMode requiredMode = RitualElementalMode.fromId(buffer.readUtf());
         return new PedestalRitualRecipe(centerItem, pedestalGroups, result, activatesSoulFire, activatesInfernalFire, requiredMode);
      }

      private static PedestalRitualRecipe createFromJson(
         Ingredient centerItem,
         List<PedestalRitualRecipe.PedestalRequirement> pedestalGroups,
         ItemStack result,
         boolean activatesSoulFire,
         boolean activatesInfernalFire,
         RitualElementalMode requiredMode
      ) {
         return new PedestalRitualRecipe(centerItem, pedestalGroups, result, activatesSoulFire, activatesInfernalFire, requiredMode);
      }

      public MapCodec<PedestalRitualRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, PedestalRitualRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
