package net.alshanex.alshanex_familiars.compat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableComponent;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FixedShrinkingRecipe extends ShrinkingRecipe {
   private final Ingredient input;
   private final ItemStack result;

   public FixedShrinkingRecipe(Ingredient input, ItemStack result) {
      this.input = input;
      this.result = result;
   }

   @Override
   public Ingredient getInputIngredient() {
      return this.input;
   }

   public boolean matches(ShrinkingRecipe.Input input, Level level) {
      if (!this.input.test(input.item())) {
         return false;
      } else {
         FamiliarConsumableComponent inputComp = (FamiliarConsumableComponent)input.item().get((DataComponentType)ComponentRegistry.FAMILIAR_CONSUMABLE.get());
         FamiliarConsumableComponent resultComp = (FamiliarConsumableComponent)this.result.get((DataComponentType)ComponentRegistry.FAMILIAR_CONSUMABLE.get());
         return resultComp == null || inputComp == null || !inputComp.equals(resultComp);
      }
   }

   public ItemStack assemble(ShrinkingRecipe.Input input, Provider registries) {
      return this.result.copy();
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeRegistry.FIXED_SHRINKING_SERIALIZER.get();
   }

   public static class Serializer implements RecipeSerializer<FixedShrinkingRecipe> {
      public static final MapCodec<FixedShrinkingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input), ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result))
            .apply(builder, FixedShrinkingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, FixedShrinkingRecipe> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC, r -> r.input, ItemStack.STREAM_CODEC, r -> r.result, FixedShrinkingRecipe::new
      );

      public MapCodec<FixedShrinkingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, FixedShrinkingRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
