package com.chadate.spellelemental.integration.jei.category;

import com.chadate.spellelemental.block.entity.SwordStandBlockEntity;
import com.chadate.spellelemental.integration.jei.SpellElementalJEIPlugin;
import com.chadate.spellelemental.integration.jei.recipe.SwordOilApplicationRecipe;
import com.chadate.spellelemental.register.ModBlocks;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class SwordOilApplicationCategory implements IRecipeCategory<SwordOilApplicationRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("spellelemental", "sword_oil_application");
   private static final ResourceLocation RIGHT_CLICK_ICON = ResourceLocation.fromNamespaceAndPath(
      "spellelemental", "textures/gui/jei/right_click_mouce_icon.png"
   );
   private final IDrawable icon;
   private static final int GUI_WIDTH = 176;
   private static final int GUI_HEIGHT = 85;
   private static final int BLOCK_RENDER_X = 38;
   private static final int BLOCK_RENDER_Y = 42;
   private static final int BLOCK_RENDER_SIZE = 40;
   private static final int INPUT_SWORD_X = 8;
   private static final int INPUT_SWORD_Y = 8;
   private static final int SWORD_OIL_X = 8;
   private static final int SWORD_OIL_Y = 33;
   private static final int OUTPUT_SWORD_X = 152;
   private static final int OUTPUT_SWORD_Y = 30;

   public SwordOilApplicationCategory(IGuiHelper guiHelper) {
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ModBlocks.SWORD_STAND.get()));
   }

   @NotNull
   public RecipeType<SwordOilApplicationRecipe> getRecipeType() {
      return SpellElementalJEIPlugin.SWORD_OIL_APPLICATION;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("jei.spellelemental.category.sword_oil_application");
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, SwordOilApplicationRecipe recipe, @NotNull IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 8, 8).addItemStack(recipe.getInputSword());
      builder.addSlot(RecipeIngredientRole.INPUT, 8, 33).addItemStack(recipe.getSwordOil());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 152, 30).addItemStack(recipe.getOutputSword());
   }

   public void draw(
      @NotNull SwordOilApplicationRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY
   ) {
      this.render3DBlock(guiGraphics, recipe);
      this.drawUsageInfo(guiGraphics);
      this.drawRightClickIcon(guiGraphics);
   }

   private void render3DBlock(GuiGraphics guiGraphics, SwordOilApplicationRecipe recipe) {
      try {
         PoseStack poseStack = guiGraphics.pose();
         poseStack.pushPose();
         poseStack.translate(58.0, 62.0, 100.0);
         poseStack.mulPose(Axis.XP.rotationDegrees(-25.0F));
         poseStack.mulPose(Axis.YP.rotationDegrees(25.0F));
         float scale = 40.0F;
         poseStack.scale(scale, -scale, scale);
         BlockState swordStandState = ModBlocks.SWORD_STAND.get().defaultBlockState();
         Minecraft minecraft = Minecraft.getInstance();
         BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
         BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
         blockRenderer.renderSingleBlock(swordStandState, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
         bufferSource.endBatch();
         this.renderSwordWithBlockEntityRenderer(poseStack, bufferSource, recipe);
         poseStack.popPose();
      } catch (Exception var9) {
         ItemStack swordStandItem = new ItemStack((ItemLike)ModBlocks.SWORD_STAND.get());
         guiGraphics.renderItem(swordStandItem, 46, 50);
      }
   }

   private void renderSwordWithBlockEntityRenderer(PoseStack poseStack, BufferSource bufferSource, SwordOilApplicationRecipe recipe) {
      try {
         ItemStack inputSword = recipe.getInputSword();
         if (!inputSword.isEmpty() && inputSword.getItem() instanceof SwordItem) {
            Minecraft minecraft = Minecraft.getInstance();
            SwordStandBlockEntity tempBlockEntity = new SwordStandBlockEntity(BlockPos.ZERO, ModBlocks.SWORD_STAND.get().defaultBlockState());
            if (minecraft.level != null) {
               tempBlockEntity.setLevel(minecraft.level);
            }

            tempBlockEntity.setSword(inputSword);
            BlockEntityRenderer<SwordStandBlockEntity> renderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(tempBlockEntity);
            if (renderer != null) {
               float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
               renderer.render(tempBlockEntity, partialTick, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
            }
         }
      } catch (Exception var9) {
      }
   }

   private void drawUsageInfo(GuiGraphics guiGraphics) {
      Minecraft minecraft = Minecraft.getInstance();
      Component usageText = Component.translatable("jei.spellelemental.usage_instruction");
      int textWidth = minecraft.font.width(usageText);
      int x = (176 - textWidth) / 2;
      guiGraphics.drawString(minecraft.font, usageText, x, 73, 6710886, false);
   }

   private void drawRightClickIcon(GuiGraphics guiGraphics) {
      int iconX = 35;
      int iconY = 33;
      int iconSize = 16;
      int iconX1 = 120;
      int iconY1 = 33;
      int iconSize1 = 16;
      RenderSystem.setShaderTexture(0, RIGHT_CLICK_ICON);
      guiGraphics.blit(RIGHT_CLICK_ICON, iconX, iconY, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
      RenderSystem.setShaderTexture(0, RIGHT_CLICK_ICON);
      guiGraphics.blit(RIGHT_CLICK_ICON, iconX1, iconY1, 0.0F, 0.0F, iconSize1, iconSize, iconSize, iconSize);
   }

   private void drawElementInfo(GuiGraphics guiGraphics, SwordOilApplicationRecipe recipe) {
      Minecraft minecraft = Minecraft.getInstance();
      Component elementText = Component.translatable(
         "jei.spellelemental.element_type", new Object[]{Component.translatable("element.spellelemental." + recipe.getElementType())}
      );
      int elementTextWidth = minecraft.font.width(elementText);
      int elementX = (176 - elementTextWidth) / 2;
      guiGraphics.drawString(minecraft.font, elementText, elementX, 90, 4210752, false);
      Component amountText = Component.translatable("jei.spellelemental.element_amount", new Object[]{recipe.getElementAmount()});
      int amountTextWidth = minecraft.font.width(amountText);
      int amountX = (176 - amountTextWidth) / 2;
      guiGraphics.drawString(minecraft.font, amountText, amountX, 100, 4210752, false);
   }

   public int getWidth() {
      return 176;
   }

   public int getHeight() {
      return 85;
   }
}
