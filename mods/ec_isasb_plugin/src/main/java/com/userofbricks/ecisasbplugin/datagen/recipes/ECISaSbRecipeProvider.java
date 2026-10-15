package com.userofbricks.ecisasbplugin.datagen.recipes;

import com.userofbricks.ecisasbplugin.item.ECISaSbItems;
import com.userofbricks.expanded_combat.init.ECItems;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class ECISaSbRecipeProvider extends RecipeProvider {
   public ECISaSbRecipeProvider(PackOutput output, CompletableFuture<Provider> provider) {
      super(output, provider);
   }

   protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ECISaSbItems.WNADERING_MAGICIAN_GAUNTLET)
         .requires(ECItems.LEATHER_GAUNTLET)
         .requires((ItemLike)ItemRegistry.ARCANE_ESSENCE.get())
         .unlockedBy(getHasName(ECItems.LEATHER_GAUNTLET), has(ECItems.LEATHER_GAUNTLET))
         .unlockedBy(getHasName((ItemLike)ItemRegistry.ARCANE_ESSENCE.get()), has((ItemLike)ItemRegistry.ARCANE_ESSENCE.get()))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ECISaSbItems.SCARECROW_GAUNTLET)
         .requires(ECItems.LEATHER_GAUNTLET)
         .requires((ItemLike)ItemRegistry.MAGIC_CLOTH.get())
         .requires(Items.HAY_BLOCK)
         .unlockedBy(getHasName(ECItems.LEATHER_GAUNTLET), has(ECItems.LEATHER_GAUNTLET))
         .unlockedBy(getHasName((ItemLike)ItemRegistry.ARCANE_ESSENCE.get()), has((ItemLike)ItemRegistry.ARCANE_ESSENCE.get()))
         .unlockedBy(getHasName(Items.HAY_BLOCK), has(Items.HAY_BLOCK))
         .save(recipeOutput);
      this.shapedGauntlet(ECISaSbItems.PYROMANCER_GAUNTLET, (ItemLike)ItemRegistry.FIRE_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(
         ECISaSbItems.ELECTROMANCER_GAUNTLET, (ItemLike)ItemRegistry.LIGHTNING_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput
      );
      this.shapedGauntlet(ECISaSbItems.ARCHEVOKER_GAUNTLET, (ItemLike)ItemRegistry.EVOCATION_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(ECISaSbItems.CULTIST_GAUNTLET, (ItemLike)ItemRegistry.BLOOD_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(ECISaSbItems.CRYOMANCER_GAUNTLET, (ItemLike)ItemRegistry.ICE_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(ECISaSbItems.SHADOW_WALKER_GAUNTLET, (ItemLike)ItemRegistry.ENDER_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(ECISaSbItems.PRIEST_GAUNTLET, (ItemLike)ItemRegistry.HOLY_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(ECISaSbItems.PLAGUED_GAUNTLET, (ItemLike)ItemRegistry.NATURE_RUNE.get(), (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
      this.shapedGauntlet(ECISaSbItems.NETHERITE_BATTLEMAGE_GAUNTLET, Items.NETHERITE_INGOT, (ItemLike)ItemRegistry.MAGIC_CLOTH.get(), recipeOutput);
   }

   protected void shapedGauntlet(@NotNull ItemLike gauntlet, @NotNull ItemLike rune, @NotNull ItemLike cloth, @NotNull RecipeOutput recipeOutput) {
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, gauntlet)
         .define('r', rune)
         .define('c', cloth)
         .pattern("rc")
         .pattern("c ")
         .unlockedBy(getHasName(cloth), has(cloth))
         .unlockedBy(getHasName(rune), has(rune))
         .save(recipeOutput);
   }
}
