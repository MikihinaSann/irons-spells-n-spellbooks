package net.alshanex.alshanex_familiars.recipe_types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.AlshanexFamiliarsMod;
import net.alshanex.alshanex_familiars.registry.RecipeRegistry;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public record RitualRecipeType(Ingredient centralItem, List<RitualRecipeType.RitualIngredient> inputs, ItemStack result, Holder<ParticleType<?>> defaultParticle)
   implements Recipe<RitualRecipeType.Input> {
   public static RitualRecipeType.Builder builder() {
      return new RitualRecipeType.Builder();
   }

   public ItemStack result() {
      return this.result.copy();
   }

   public boolean matches(RitualRecipeType.Input input, Level level) {
      if (!this.centralItem.test(input.centralItem())) {
         return false;
      } else {
         List<ItemStack> availableItems = new ArrayList<>();

         for (ItemStack stack : input.inputItems()) {
            if (!stack.isEmpty()) {
               availableItems.add(stack.copy());
            }
         }

         int totalAvailable = availableItems.stream().mapToInt(ItemStack::getCount).sum();
         int totalRequired = this.inputs.stream().mapToInt(RitualRecipeType.RitualIngredient::count).sum();
         if (totalAvailable != totalRequired) {
            return false;
         } else {
            for (RitualRecipeType.RitualIngredient recipeIngredient : this.inputs) {
               int remainingCount = recipeIngredient.count();

               for (ItemStack available : availableItems) {
                  if (!available.isEmpty() && available.getCount() > 0 && recipeIngredient.ingredient().test(available)) {
                     int toConsume = Math.min(remainingCount, available.getCount());
                     available.shrink(toConsume);
                     remainingCount -= toConsume;
                     if (remainingCount <= 0) {
                        break;
                     }
                  }
               }

               if (remainingCount > 0) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public ItemStack assemble(RitualRecipeType.Input input, Provider registries) {
      return this.result.copy();
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return false;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.result.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)RecipeRegistry.RITUAL_RECIPE_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)RecipeRegistry.RITUAL_RECIPE_TYPE.get();
   }

   public static class Builder implements RecipeBuilder {
      ParticleType<?> defaultParticle = ParticleTypes.ASH;
      Ingredient centralItem = null;
      List<RitualRecipeType.RitualIngredient> inputs = new ArrayList<>();
      ItemStack result = null;

      public RitualRecipeType.Builder withInput(Ingredient input, int count, boolean consume) {
         this.inputs.add(new RitualRecipeType.RitualIngredient(input, count, consume));
         return this;
      }

      public RitualRecipeType.Builder withInput(Ingredient input) {
         return this.withInput(input, 1, true);
      }

      public RitualRecipeType.Builder withInput(Item input, int count, boolean consume) {
         return this.withInput(Ingredient.of(new ItemLike[]{input}), count, consume);
      }

      public RitualRecipeType.Builder withInput(Item input) {
         return this.withInput(Ingredient.of(new ItemLike[]{input}), 1, true);
      }

      public RitualRecipeType.Builder withCentralItem(Item item) {
         this.centralItem = Ingredient.of(new ItemLike[]{item});
         return this;
      }

      public RitualRecipeType.Builder withCentralItem(Ingredient ingredient) {
         this.centralItem = ingredient;
         return this;
      }

      public RitualRecipeType.Builder withReturnItem(Item returned) {
         this.result = new ItemStack(returned);
         return this;
      }

      public RitualRecipeType.Builder withParticles(ParticleType<?> particle) {
         this.defaultParticle = particle;
         return this;
      }

      public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
         return this;
      }

      public RecipeBuilder group(@Nullable String groupName) {
         return this;
      }

      public Item getResult() {
         return this.result.getItem();
      }

      public void save(RecipeOutput recipeOutput) {
         ResourceLocation id = AlshanexFamiliarsMod.id("ritual_recipes/" + BuiltInRegistries.ITEM.getKey(this.result.getItem()).getPath());
         this.save(recipeOutput, id);
      }

      public void save(RecipeOutput recipeOutput, ResourceLocation id) {
         recipeOutput.accept(
            id, new RitualRecipeType(this.centralItem, this.inputs, this.result, BuiltInRegistries.PARTICLE_TYPE.wrapAsHolder(this.defaultParticle)), null
         );
      }
   }

   public record Input(ItemStack centralItem, List<ItemStack> inputItems) implements RecipeInput {
      public ItemStack getItem(int index) {
         if (index == 0) {
            return this.centralItem;
         } else {
            return index - 1 < this.inputItems.size() ? this.inputItems.get(index - 1) : ItemStack.EMPTY;
         }
      }

      public int size() {
         return 1 + this.inputItems.size();
      }
   }

   public record RitualIngredient(Ingredient ingredient, int count, boolean consume) {
      public static final Codec<RitualRecipeType.RitualIngredient> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Ingredient.CODEC.fieldOf("ingredient").forGetter(RitualRecipeType.RitualIngredient::ingredient),
               Codec.INT.optionalFieldOf("count", 1).forGetter(RitualRecipeType.RitualIngredient::count),
               Codec.BOOL.optionalFieldOf("consume", true).forGetter(RitualRecipeType.RitualIngredient::consume)
            )
            .apply(instance, RitualRecipeType.RitualIngredient::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, RitualRecipeType.RitualIngredient> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC,
         RitualRecipeType.RitualIngredient::ingredient,
         ByteBufCodecs.VAR_INT,
         RitualRecipeType.RitualIngredient::count,
         ByteBufCodecs.BOOL,
         RitualRecipeType.RitualIngredient::consume,
         RitualRecipeType.RitualIngredient::new
      );

      public boolean test(ItemStack stack) {
         return this.ingredient.test(stack) && stack.getCount() >= this.count;
      }
   }

   public static class Serializer implements RecipeSerializer<RitualRecipeType> {
      public static final MapCodec<RitualRecipeType> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Ingredient.CODEC.fieldOf("central_item").forGetter(RitualRecipeType::centralItem),
               RitualRecipeType.RitualIngredient.CODEC.listOf().fieldOf("inputs").forGetter(RitualRecipeType::inputs),
               ItemStack.CODEC.fieldOf("result").forGetter(RitualRecipeType::result),
               BuiltInRegistries.PARTICLE_TYPE
                  .holderByNameCodec()
                  .optionalFieldOf("particle_type", BuiltInRegistries.PARTICLE_TYPE.wrapAsHolder(ParticleTypes.ASH))
                  .forGetter(RitualRecipeType::defaultParticle)
            )
            .apply(builder, RitualRecipeType::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, RitualRecipeType> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC,
         RitualRecipeType::centralItem,
         RitualRecipeType.RitualIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),
         RitualRecipeType::inputs,
         ItemStack.STREAM_CODEC,
         RitualRecipeType::result,
         ByteBufCodecs.holderRegistry(Registries.PARTICLE_TYPE),
         RitualRecipeType::defaultParticle,
         RitualRecipeType::new
      );

      public MapCodec<RitualRecipeType> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, RitualRecipeType> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
