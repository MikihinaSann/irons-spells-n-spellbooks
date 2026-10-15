package com.somake.somakespells.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.block.entity.UpgradeForgeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;

public class UpgradeForgeBlockEntityRenderer implements BlockEntityRenderer<UpgradeForgeBlockEntity> {
   private static final float DEFAULT_BOOK_TRANSLATE_X = 0.79F;
   private static final float DEFAULT_BOOK_TRANSLATE_Y = 1.2F;
   private static final float DEFAULT_BOOK_TRANSLATE_Z = 0.28F;
   private static final float DEFAULT_BOOK_ROTATION_X = 0.0F;
   private static final float DEFAULT_BOOK_ROTATION_Y = 180.0F;
   private static final float DEFAULT_BOOK_SCALE = 0.6F;
   private static float bookTranslateX = 0.79F;
   private static float bookTranslateY = 1.2F;
   private static float bookTranslateZ = 0.28F;
   private static float bookRotationX = 0.0F;
   private static float bookRotationY = 180.0F;
   private static float bookScale = 0.6F;
   private static final float BOOK_CENTER_X = 0.5F;
   private static final float BOOK_CENTER_Z = 0.5F;
   private final ItemRenderer itemRenderer;

   public UpgradeForgeBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(UpgradeForgeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      BlockState state = blockEntity.getBlockState();
      if (state.hasProperty(BlockStateProperties.CHEST_TYPE) && state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.RIGHT) {
         ItemStack book = blockEntity.getItem(0);
         if (!book.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(facingRotation(state)));
            poseStack.translate(bookTranslateX - 0.5F, bookTranslateY, bookTranslateZ - 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(bookRotationY));
            poseStack.mulPose(Axis.XP.rotationDegrees(bookRotationX));
            poseStack.scale(bookScale, bookScale, bookScale);
            this.itemRenderer.renderStatic(book, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
            poseStack.popPose();
         }
      }
   }

   private static float facingRotation(BlockState state) {
      return switch ((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
         case SOUTH -> 180.0F;
         case WEST -> 90.0F;
         case NORTH -> 0.0F;
         case EAST -> 270.0F;
         default -> 0.0F;
      };
   }

   public static void setBookTranslation(float x, float y, float z) {
      bookTranslateX = x;
      bookTranslateY = y;
      bookTranslateZ = z;
   }

   public static void setBookRotation(float x, float y) {
      bookRotationX = x;
      bookRotationY = y;
   }

   public static void setBookScale(float scale) {
      bookScale = scale;
   }

   public static void resetBookTransform() {
      bookTranslateX = 0.79F;
      bookTranslateY = 1.2F;
      bookTranslateZ = 0.28F;
      bookRotationX = 0.0F;
      bookRotationY = 180.0F;
      bookScale = 0.6F;
   }

   public static String describeTransform() {
      return String.format(
         "pos=(%.4f, %.4f, %.4f) rot=(%.2f, %.2f) scale=%.4f", bookTranslateX, bookTranslateY, bookTranslateZ, bookRotationX, bookRotationY, bookScale
      );
   }
}
