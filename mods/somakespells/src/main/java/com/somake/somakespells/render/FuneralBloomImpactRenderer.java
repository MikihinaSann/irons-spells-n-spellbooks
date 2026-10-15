package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.FuneralBloomImpactEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FuneralBloomImpactRenderer extends EntityRenderer<FuneralBloomImpactEntity> {
   private static final ResourceLocation CROWN = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/withered_rose_vortex_crown.png");

   public FuneralBloomImpactRenderer(Context context) {
      super(context);
   }

   public void render(FuneralBloomImpactEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
      float open = entity.getOpenProgress(partialTick);
      float scale = entity.getScale() * (0.35F + open * 1.35F);
      int alpha = Mth.clamp((int)(open * 235.0F), 0, 255);
      if (alpha > 0) {
         FuneralBloomRenderUtil.groundQuad(poseStack, buffers, CROWN, scale, 0.035, 255, 255, 255, alpha, true);
         super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
      }
   }

   public boolean shouldRender(FuneralBloomImpactEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
      return true;
   }

   public ResourceLocation getTextureLocation(FuneralBloomImpactEntity entity) {
      return CROWN;
   }
}
