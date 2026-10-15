package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.BloodyLegacyVisualEntity;
import com.somake.somakespells.entity.model.BloodyLegacyVisualModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodyLegacyVisualRenderer extends GeoEntityRenderer<BloodyLegacyVisualEntity> {
   private static final float HEIGHT_SCALE = 2.45F;

   public BloodyLegacyVisualRenderer(Context renderManager) {
      super(renderManager, new BloodyLegacyVisualModel());
      this.shadowRadius = 0.0F;
   }

   public void render(BloodyLegacyVisualEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F - entity.getYRot()));
      poseStack.scale(entity.getLengthScale(), 2.45F, 1.0F);
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);
      poseStack.popPose();
   }
}
