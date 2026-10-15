package com.chadate.spellelemental.integration.jei.category;

import com.chadate.spellelemental.integration.jei.recipe.ElementReactionRecipe;
import com.chadate.spellelemental.register.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class ElementReactionCategory implements IRecipeCategory<ElementReactionRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("spellelemental", "element_reaction");
   public static final RecipeType<ElementReactionRecipe> RECIPE_TYPE = RecipeType.create("spellelemental", "element_reaction", ElementReactionRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;

   public ElementReactionCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(160, 100);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ModItems.STELLAR_FRAGMENT.get()));
   }

   @NotNull
   public RecipeType<ElementReactionRecipe> getRecipeType() {
      return RECIPE_TYPE;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("jei.spellelemental.category.element_reaction");
   }

   @NotNull
   public IDrawable getBackground() {
      return this.background;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ElementReactionRecipe recipe, @NotNull IFocusGroup focuses) {
   }

   public void draw(
      @NotNull ElementReactionRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY
   ) {
      Minecraft mc = Minecraft.getInstance();
      Font font = mc.font;
      int yOffset = 5;
      Component titleComponent = Component.translatable("reaction.spellelemental." + recipe.getReactionId());
      int titleWidth = font.width(titleComponent);
      guiGraphics.drawString(font, titleComponent, (160 - titleWidth) / 2, yOffset, 16777215, true);
      yOffset += 15;
      this.drawElementCombination(guiGraphics, font, recipe, yOffset);
      yOffset += 25;
      Component triggerText = Component.translatable("jei.spellelemental.trigger_type." + recipe.getTriggerType());
      guiGraphics.drawString(font, triggerText, 5, yOffset, 11184810, false);
      yOffset += 12;

      for (ElementReactionRecipe.EffectInfo effect : recipe.getEffects()) {
         Component effectDesc = effect.getDescription().startsWith("jei.")
            ? Component.translatable(effect.getDescription())
            : Component.literal(effect.getDescription());
         Component effectText = Component.literal("• ").append(effectDesc);
         guiGraphics.drawString(font, effectText, 5, yOffset, 16768324, false);
         yOffset += 10;
      }

      if (recipe.getDescription() != null && !recipe.getDescription().isEmpty()) {
         yOffset += 3;
         Component descText = recipe.getDescription().startsWith("jei.")
            ? Component.translatable(recipe.getDescription())
            : Component.literal(recipe.getDescription());
         String descString = descText.getString();
         if (font.width(descString) > 150) {
            String[] words = descString.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
               if (font.width(line + word) > 150) {
                  guiGraphics.drawString(font, Component.literal(line.toString()), 5, yOffset, 8947848, false);
                  yOffset += 9;
                  line = new StringBuilder(word + " ");
               } else {
                  line.append(word).append(" ");
               }
            }

            if (line.length() > 0) {
               guiGraphics.drawString(font, Component.literal(line.toString()), 5, yOffset, 8947848, false);
            }
         } else {
            guiGraphics.drawString(font, descText, 5, yOffset, 8947848, false);
         }
      }
   }

   private void drawElementCombination(GuiGraphics guiGraphics, Font font, ElementReactionRecipe recipe, int yOffset) {
      int xStart = 20;

      try {
         guiGraphics.blit(recipe.getElement1Icon(), xStart, yOffset, 0.0F, 0.0F, 16, 16, 16, 16);
      } catch (Exception var9) {
         guiGraphics.drawString(font, Component.literal(recipe.getElement1()), xStart, yOffset, 16777215, false);
      }

      String symbol = recipe.isDirectional() ? "→" : "+";
      guiGraphics.drawString(font, Component.literal(symbol), xStart + 20, yOffset + 4, 16777215, false);

      try {
         guiGraphics.blit(recipe.getElement2Icon(), xStart + 35, yOffset, 0.0F, 0.0F, 16, 16, 16, 16);
      } catch (Exception var8) {
         guiGraphics.drawString(font, Component.literal(recipe.getElement2()), xStart + 35, yOffset, 16777215, false);
      }

      guiGraphics.drawString(font, Component.literal("="), xStart + 55, yOffset + 4, 16777215, false);
      Component reactionName = Component.translatable("reaction.spellelemental." + recipe.getReactionId());
      guiGraphics.drawString(font, reactionName, xStart + 70, yOffset + 4, 16768256, false);
   }
}
