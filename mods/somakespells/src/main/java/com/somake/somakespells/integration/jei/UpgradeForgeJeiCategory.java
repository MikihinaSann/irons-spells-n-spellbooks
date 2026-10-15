package com.somake.somakespells.integration.jei;

import com.somake.somakespells.item.DormantGrimoireProgressHelper;
import com.somake.somakespells.item.spellbooks.SpellbookProgressHelper;
import com.somake.somakespells.recipe.upgrade_forge.UpgradeForgeRecipe;
import com.somake.somakespells.registries.ModItems;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UpgradeForgeJeiCategory implements IRecipeCategory<UpgradeForgeRecipe> {
   public static final RecipeType<UpgradeForgeRecipe> RECIPE_TYPE = RecipeType.create("somakespells", "upgrade_forge", UpgradeForgeRecipe.class);
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/gui/upgrade_forge_jei.png");
   private static final int TEXTURE_WIDTH = 256;
   private static final int TEXTURE_HEIGHT = 256;
   private static final int BACKGROUND_WIDTH = 176;
   private static final int BACKGROUND_HEIGHT = 64;
   private static final int BOOK_SLOT_X = 17;
   private static final int BOOK_SLOT_Y = 24;
   private static final int RESULT_SLOT_X = 134;
   private static final int RESULT_SLOT_Y = 24;
   private static final int[][] INGREDIENT_POSITIONS = new int[][]{{70, 6}, {52, 24}, {70, 42}, {88, 24}};
   private final IDrawable background;
   private final IDrawable icon;

   public UpgradeForgeJeiCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(176, 64);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack((ItemLike)ModItems.UPGRADE_FORGE.get()));
   }

   public RecipeType<UpgradeForgeRecipe> getRecipeType() {
      return RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("block.somakespells.upgrade_forge");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, UpgradeForgeRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 17, 24).addItemStacks(this.toDisplayBaseBooks(recipe));
      List<UpgradeForgeRecipe.CountedIngredient> ingredients = recipe.ingredients();

      for (int i = 0; i < ingredients.size() && i < INGREDIENT_POSITIONS.length; i++) {
         int[] position = INGREDIENT_POSITIONS[i];
         builder.addSlot(RecipeIngredientRole.INPUT, position[0], position[1]).addItemStacks(this.toDisplayStacks(ingredients.get(i)));
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 134, 24).addItemStack(recipe.resultBook().copy());
   }

   public void draw(UpgradeForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      guiGraphics.blit(TEXTURE, 0, 0, 0.0F, 0.0F, 176, 64, 256, 256);
   }

   public int getWidth() {
      return 176;
   }

   public int getHeight() {
      return 64;
   }

   private List<ItemStack> toDisplayStacks(UpgradeForgeRecipe.CountedIngredient ingredient) {
      return Arrays.stream(ingredient.ingredient().getItems()).<ItemStack>map(ItemStack::copy).peek(stack -> stack.setCount(ingredient.count())).toList();
   }

   private List<ItemStack> toDisplayBaseBooks(UpgradeForgeRecipe recipe) {
      return Arrays.stream(recipe.baseBook().getItems())
         .map(
            stack -> recipe.requiredGrimoirePath().isPresent()
               ? DormantGrimoireProgressHelper.createAlignmentPreview(stack, recipe.requiredGrimoirePath().get())
               : (recipe.requiresMaxProgress() ? SpellbookProgressHelper.createMaxProgressPreview(stack) : stack.copy())
         )
         .toList();
   }
}
