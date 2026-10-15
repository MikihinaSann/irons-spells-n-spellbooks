package com.somake.somakespells.recipe.upgrade_forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModRecipes;
import io.netty.handler.codec.DecoderException;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record UpgradeForgeRecipe(
   Ingredient baseBook,
   ItemStack resultBook,
   ResourceLocation school,
   boolean requiresMaxProgress,
   Optional<String> requiredGrimoirePath,
   List<UpgradeForgeRecipe.CountedIngredient> ingredients
) implements Recipe<UpgradeForgeRecipe.Input> {
   private static final Codec<List<UpgradeForgeRecipe.CountedIngredient>> INGREDIENTS_CODEC = UpgradeForgeRecipe.CountedIngredient.CODEC
      .codec()
      .listOf()
      .flatXmap(
         list -> list.size() == 4 ? DataResult.success(list) : DataResult.error(() -> "Upgrade Forge recipes must define exactly 4 ingredients"),
         DataResult::success
      );

   public boolean matchesBase(ItemStack stack) {
      return stack != null && !stack.isEmpty() && this.baseBook.test(stack);
   }

   public boolean matches(UpgradeForgeRecipe.Input input, Level level) {
      if (input != null && input.materials() != null && input.materials().size() == this.ingredients.size() && this.matchesBase(input.bookStack())) {
         for (int i = 0; i < this.ingredients.size(); i++) {
            if (!this.ingredients.get(i).test(input.material(i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(UpgradeForgeRecipe.Input input, Provider registries) {
      return input == null ? this.resultBook.copy() : this.createUpgradedBook(input.bookStack());
   }

   public ItemStack createUpgradedBook(ItemStack sourceBook) {
      ItemStack upgraded = this.resultBook.copy();
      if (!upgraded.isEmpty() && sourceBook != null && !sourceBook.isEmpty()) {
         upgraded.applyComponents(
            sourceBook.getComponentsPatch()
               .forget(component -> component == ComponentRegistry.SPELL_CONTAINER.get() || component == ComponentRegistry.AFFINITY_COMPONENT.get())
         );
         copySpells(sourceBook, upgraded);
         return upgraded;
      } else {
         return upgraded;
      }
   }

   private static void copySpells(ItemStack sourceBook, ItemStack upgraded) {
      ISpellContainer sourceContainer = ISpellContainer.get(sourceBook);
      ISpellContainer targetContainer = ISpellContainer.get(upgraded);
      if (sourceContainer != null && targetContainer != null) {
         ISpellContainerMutable mutableTarget = targetContainer.mutableCopy();

         for (SpellSlot spellSlot : sourceContainer.getActiveSpells()) {
            if (spellSlot != null && spellSlot.getSpell() != null) {
               boolean copiedAtOriginalIndex = spellSlot.index() >= 0
                  && spellSlot.index() < mutableTarget.getMaxSpellCount()
                  && mutableTarget.addSpellAtIndex(spellSlot.getSpell(), spellSlot.getLevel(), spellSlot.index(), spellSlot.isLocked());
               if (!copiedAtOriginalIndex) {
                  mutableTarget.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), spellSlot.isLocked());
               }
            }
         }

         ISpellContainer.set(upgraded, mutableTarget.toImmutable());
      }
   }

   public boolean canCraftInDimensions(int width, int height) {
      return false;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.resultBook.copy();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.UPGRADE_FORGE_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipes.UPGRADE_FORGE_TYPE.get();
   }

   public boolean isSpecial() {
      return true;
   }

   public record CountedIngredient(Ingredient ingredient, int count) {
      public static final MapCodec<UpgradeForgeRecipe.CountedIngredient> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Ingredient.CODEC.fieldOf("ingredient").forGetter(UpgradeForgeRecipe.CountedIngredient::ingredient),
               Codec.intRange(1, 99).fieldOf("count").forGetter(UpgradeForgeRecipe.CountedIngredient::count)
            )
            .apply(builder, UpgradeForgeRecipe.CountedIngredient::new)
      );

      public boolean test(ItemStack stack) {
         return stack != null && this.count > 0 && stack.getCount() >= this.count && this.ingredient.test(stack);
      }

      public ItemStack displayStack() {
         ItemStack[] options = this.ingredient.getItems();
         if (options.length == 0) {
            return ItemStack.EMPTY;
         } else {
            ItemStack display = options[0].copy();
            display.setCount(this.count);
            return display;
         }
      }

      private static void toNetwork(RegistryFriendlyByteBuf buffer, UpgradeForgeRecipe.CountedIngredient ingredient) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient.ingredient());
         buffer.writeVarInt(ingredient.count());
      }

      private static UpgradeForgeRecipe.CountedIngredient fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         int count = buffer.readVarInt();
         if (count >= 1 && count <= 99) {
            return new UpgradeForgeRecipe.CountedIngredient(ingredient, count);
         } else {
            throw new DecoderException("Invalid Upgrade Forge ingredient count: " + count);
         }
      }
   }

   public record Input(ItemStack bookStack, List<ItemStack> materials) implements RecipeInput {
      public ItemStack getItem(int index) {
         if (index == 0) {
            return this.bookStack;
         } else {
            return this.materials != null && index > 0 && index <= this.materials.size() ? this.materials.get(index - 1) : ItemStack.EMPTY;
         }
      }

      public int size() {
         return 1 + (this.materials == null ? 0 : this.materials.size());
      }

      public ItemStack material(int index) {
         return this.materials != null && index >= 0 && index < this.materials.size() ? this.materials.get(index) : ItemStack.EMPTY;
      }
   }

   public static class Serializer implements RecipeSerializer<UpgradeForgeRecipe> {
      public static final MapCodec<UpgradeForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Ingredient.CODEC.fieldOf("base_book").forGetter(UpgradeForgeRecipe::baseBook),
               ItemStack.SINGLE_ITEM_CODEC.fieldOf("result_book").forGetter(UpgradeForgeRecipe::resultBook),
               ResourceLocation.CODEC.fieldOf("school").forGetter(UpgradeForgeRecipe::school),
               Codec.BOOL.optionalFieldOf("requires_max_progress", true).forGetter(UpgradeForgeRecipe::requiresMaxProgress),
               Codec.STRING.optionalFieldOf("required_grimoire_path").forGetter(UpgradeForgeRecipe::requiredGrimoirePath),
               UpgradeForgeRecipe.INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(UpgradeForgeRecipe::ingredients)
            )
            .apply(builder, UpgradeForgeRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeForgeRecipe> STREAM_CODEC = StreamCodec.of(
         UpgradeForgeRecipe.Serializer::toNetwork, UpgradeForgeRecipe.Serializer::fromNetwork
      );

      private static void toNetwork(RegistryFriendlyByteBuf buffer, UpgradeForgeRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.baseBook());
         ItemStack.STREAM_CODEC.encode(buffer, recipe.resultBook());
         buffer.writeResourceLocation(recipe.school());
         buffer.writeBoolean(recipe.requiresMaxProgress());
         buffer.writeOptional(recipe.requiredGrimoirePath(), (buf, value) -> buf.writeUtf(value));
         buffer.writeVarInt(recipe.ingredients().size());

         for (UpgradeForgeRecipe.CountedIngredient ingredient : recipe.ingredients()) {
            UpgradeForgeRecipe.CountedIngredient.toNetwork(buffer, ingredient);
         }
      }

      private static UpgradeForgeRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Ingredient baseBook = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         ItemStack resultBook = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         ResourceLocation school = buffer.readResourceLocation();
         boolean requiresMaxProgress = buffer.readBoolean();
         Optional<String> requiredGrimoirePath = buffer.readOptional(buf -> buf.readUtf());
         int count = buffer.readVarInt();
         if (count != 4) {
            throw new DecoderException("Upgrade Forge recipes must define exactly 4 ingredients, got " + count);
         } else {
            List<UpgradeForgeRecipe.CountedIngredient> ingredients = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
               ingredients.add(UpgradeForgeRecipe.CountedIngredient.fromNetwork(buffer));
            }

            return new UpgradeForgeRecipe(baseBook, resultBook, school, requiresMaxProgress, requiredGrimoirePath, ingredients);
         }
      }

      public MapCodec<UpgradeForgeRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, UpgradeForgeRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
