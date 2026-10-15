package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.MirrorStrikeVisualEntity;
import com.somake.somakespells.entity.model.MirrorStrikeVisualModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MirrorStrikeVisualRenderer extends GeoEntityRenderer<MirrorStrikeVisualEntity> {
   public MirrorStrikeVisualRenderer(Context renderManager) {
      super(renderManager, new MirrorStrikeVisualModel());
   }

   public void render(MirrorStrikeVisualEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      float radius = Math.max(0.0F, entity.getRadius() - 1.5F);
      poseStack.scale(radius, 1.0F, radius);
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);
      poseStack.popPose();
   }
}
