package com.somake.somakespells.integration.jei;

import com.somake.somakespells.item.ElementalChargeNecklaceItem;
import com.somake.somakespells.recipe.pedestal_ritual.PedestalRitualRecipe;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.ritual.RitualElementalMode;
import java.util.ArrayList;
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

public class PedestalRitualJeiCategory implements IRecipeCategory<PedestalRitualRecipe> {
   public static final RecipeType<PedestalRitualRecipe> RECIPE_TYPE = RecipeType.create("somakespells", "pedestal_ritual", PedestalRitualRecipe.class);
   private static final ResourceLocation FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/gui/fire_ritual_jei.png");
   private static final ResourceLocation SOUL_FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/gui/soul_fire_ritual_jei.png");
   private static final ResourceLocation INFERNAL_FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/gui/infernal_fire_ritual_jei.png"
   );
   private static final int TEXTURE_WIDTH = 256;
   private static final int TEXTURE_HEIGHT = 256;
   private static final int BACKGROUND_WIDTH = 176;
   private static final int BACKGROUND_HEIGHT = 144;
   private static final int LEFT_GROUP_X = 22;
   private static final int RIGHT_GROUP_X = 119;
   private static final int GROUP_START_Y = 18;
   private static final int GROUP_ROW_SPACING = 23;
   private static final int GROUP_INGREDIENT_SPACING = 19;
   private static final int CENTER_X = 80;
   private static final int CENTER_Y = 42;
   private static final int RESULT_X = 80;
   private static final int RESULT_Y = 84;
   private final IDrawable background;
   private final IDrawable icon;

   public PedestalRitualJeiCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(176, 144);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack((ItemLike)ModItems.PEDESTAL_OF_FIRE.get()));
   }

   public RecipeType<PedestalRitualRecipe> getRecipeType() {
      return RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("jei.category.somakespells.pedestal_ritual");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, PedestalRitualRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 80, 42).addItemStacks(this.toDisplayCenterStacks(recipe));
      int leftColumnGroupCount = (recipe.pedestalGroups().size() + 1) / 2;

      for (int i = 0; i < recipe.pedestalGroups().size(); i++) {
         boolean leftColumn = i < leftColumnGroupCount;
         int rowIndex = leftColumn ? i : i - leftColumnGroupCount;
         int baseX = leftColumn ? 22 : 119;
         int baseY = 18 + rowIndex * 23;
         List<PedestalRitualRecipe.CountedIngredient> ingredients = recipe.pedestalGroups().get(i).ingredients();

         for (int ingredientIndex = 0; ingredientIndex < ingredients.size(); ingredientIndex++) {
            builder.addSlot(RecipeIngredientRole.INPUT, baseX + ingredientIndex * 19, baseY)
               .addItemStacks(this.toDisplayStacks(ingredients.get(ingredientIndex)));
         }
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 84).addItemStack(recipe.result().copy());
   }

   public void draw(PedestalRitualRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      guiGraphics.blit(this.resolveTexture(recipe.requiredMode()), 0, 0, 0.0F, 0.0F, 176, 144, 256, 256);
   }

   private ResourceLocation resolveTexture(RitualElementalMode mode) {
      return switch (mode) {
         case SOUL_FIRE -> SOUL_FIRE_TEXTURE;
         case INFERNAL_FIRE -> INFERNAL_FIRE_TEXTURE;
         default -> FIRE_TEXTURE;
      };
   }

   private List<ItemStack> toDisplayStacks(PedestalRitualRecipe.CountedIngredient ingredient) {
      List<ItemStack> displayStacks = new ArrayList<>();
      Arrays.stream(ingredient.ingredient().getItems())
         .<ItemStack>map(ItemStack::copy)
         .peek(stack -> stack.setCount(ingredient.count()))
         .forEach(displayStacks::add);
      return displayStacks;
   }

   private List<ItemStack> toDisplayCenterStacks(PedestalRitualRecipe recipe) {
      return Arrays.stream(recipe.centerItem().getItems())
         .<ItemStack>map(ItemStack::copy)
         .map(stack -> this.createRechargePreviewIfNeeded(recipe, stack))
         .toList();
   }

   private ItemStack createRechargePreviewIfNeeded(PedestalRitualRecipe recipe, ItemStack stack) {
      if (!this.isRechargeRecipe(recipe)) {
         return stack;
      } else {
         if (stack.getItem() instanceof ElementalChargeNecklaceItem necklace) {
            necklace.setCurrentCharges(stack, 0);
         }

         return stack;
      }
   }

   private boolean isRechargeRecipe(PedestalRitualRecipe recipe) {
      return recipe.result().getItem() instanceof ElementalChargeNecklaceItem
         && Arrays.stream(recipe.centerItem().getItems()).anyMatch(stack -> stack.getItem() == recipe.result().getItem());
   }
}
