package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.SeaSerpentJetEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SeaSerpentJetRenderer extends GeoEntityRenderer<SeaSerpentJetEntity> {
   public SeaSerpentJetRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel<SeaSerpentJetEntity>(ResourceLocation.fromNamespaceAndPath("somakespells", "sea_serpent_jet"), true) {
         public ResourceLocation getTextureResource(SeaSerpentJetEntity animatable) {
            return ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/scylla/storm_serpent.png");
         }
      });
   }

   public void render(SeaSerpentJetEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      poseStack.popPose();
   }
}
