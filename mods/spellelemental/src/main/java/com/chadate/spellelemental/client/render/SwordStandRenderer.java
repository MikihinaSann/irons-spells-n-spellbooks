package com.chadate.spellelemental.client.render;

import com.chadate.spellelemental.block.SwordStandBlock;
import com.chadate.spellelemental.block.entity.SwordStandBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SwordStandRenderer implements BlockEntityRenderer<SwordStandBlockEntity> {
   private final ItemRenderer itemRenderer;

   public SwordStandRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      SwordStandBlockEntity blockEntity,
      float partialTick,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      ItemStack sword = blockEntity.getSword();
      if (!sword.isEmpty() && sword.getItem() instanceof SwordItem) {
         BlockState blockState = blockEntity.getBlockState();
         Direction facing = (Direction)blockState.getValue(SwordStandBlock.FACING);
         poseStack.pushPose();
         long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
         float floatOffset = (float)Math.sin(((float)gameTime + partialTick) * 0.1) * 0.1F;
         poseStack.translate(0.5, 1.0 + floatOffset, 0.5);

         float yRotation = switch (facing) {
            case NORTH -> 0.0F;
            case SOUTH -> 180.0F;
            case EAST -> 90.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
         };
         poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
         poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));
         BakedModel model = this.itemRenderer.getModel(sword, blockEntity.getLevel(), null, 0);
         this.itemRenderer.render(sword, ItemDisplayContext.FIXED, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);
         poseStack.popPose();
      }
   }

   public int getViewDistance() {
      return 64;
   }
}
