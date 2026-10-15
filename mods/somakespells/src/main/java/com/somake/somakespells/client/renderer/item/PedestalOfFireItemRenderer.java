package com.somake.somakespells.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.block.entity.PedestalOfFireBlockEntity;
import com.somake.somakespells.registries.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class PedestalOfFireItemRenderer extends BlockEntityWithoutLevelRenderer {
   private final PedestalOfFireBlockEntity pedestal = new PedestalOfFireBlockEntity(
      BlockPos.ZERO, ((Block)ModBlocks.PEDESTAL_OF_FIRE.get()).defaultBlockState()
   );

   public PedestalOfFireItemRenderer() {
      this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
   }

   public PedestalOfFireItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
      super(dispatcher, modelSet);
   }

   public void renderByItem(
      ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.pedestal, poseStack, buffer, packedLight, packedOverlay);
   }
}
