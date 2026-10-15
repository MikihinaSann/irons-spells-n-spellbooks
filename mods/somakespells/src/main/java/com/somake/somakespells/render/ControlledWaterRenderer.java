package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.ControlledWaterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ControlledWaterRenderer extends GeoEntityRenderer<ControlledWaterEntity> {
   public ControlledWaterRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel<ControlledWaterEntity>(ResourceLocation.fromNamespaceAndPath("somakespells", "waterball"), true) {
         public ResourceLocation getTextureResource(ControlledWaterEntity animatable) {
            return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/projectile/waterball/waterball.png");
         }
      });
   }

   public void render(ControlledWaterEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      poseStack.scale(0.3F, 0.3F, 0.3F);
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);
      poseStack.popPose();
   }
}
