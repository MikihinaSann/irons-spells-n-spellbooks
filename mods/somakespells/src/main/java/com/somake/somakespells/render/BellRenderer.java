package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somake.somakespells.entity.BellEntity;
import com.somake.somakespells.entity.model.BellModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BellRenderer extends GeoEntityRenderer<BellEntity> {
   private static final double MODEL_HEIGHT_OFFSET = 0.85;

   public BellRenderer(Context renderManager) {
      super(renderManager, new BellModel());
      this.shadowRadius = 0.45F;
   }

   public void preRender(
      PoseStack poseStack,
      BellEntity animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.translate(0.0, 0.85, 0.0);
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }
}
