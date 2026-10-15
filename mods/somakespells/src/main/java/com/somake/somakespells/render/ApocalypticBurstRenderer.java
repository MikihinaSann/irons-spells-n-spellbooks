package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.ApocalypticBurstEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ApocalypticBurstRenderer extends EntityRenderer<ApocalypticBurstEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/flame_strike_sigil.png");
   private static final ResourceLocation SOUL_TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/soul_flame_strike_sigil.png");
   private static final ResourceLocation INFERNAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/infernal_flame_strike_sigil.png"
   );

   public ApocalypticBurstRenderer(Context context) {
      super(context);
   }

   public void render(ApocalypticBurstEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      float radius = entity.getLerpedRadius(partialTicks);
      if (radius > 0.0F) {
         poseStack.pushPose();
         poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
         poseStack.translate(0.0, 0.02, 0.0);
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
         Matrix4f matrix4f = poseStack.last().pose();
         Matrix3f matrix3f = poseStack.last().normal();
         float size = radius * 2.0F;
         float halfSize = size / 2.0F;
         boolean isSoul = entity.isSoulFire();
         boolean isInfernal = entity.isInfernalFire();
         vertex(consumer, matrix4f, matrix3f, light, -halfSize, 0.0F, -halfSize, 0.0F, 0.0F, isSoul, isInfernal);
         vertex(consumer, matrix4f, matrix3f, light, -halfSize, 0.0F, halfSize, 0.0F, 1.0F, isSoul, isInfernal);
         vertex(consumer, matrix4f, matrix3f, light, halfSize, 0.0F, halfSize, 1.0F, 1.0F, isSoul, isInfernal);
         vertex(consumer, matrix4f, matrix3f, light, halfSize, 0.0F, -halfSize, 1.0F, 0.0F, isSoul, isInfernal);
         poseStack.popPose();
      }

      super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, light);
   }

   private static void vertex(
      VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int light, float x, float y, float z, float u, float v, boolean isSoul, boolean isInfernal
   ) {
      consumer.addVertex(pose, x, y, z)
         .setColor(255, 255, 255, 255)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(ApocalypticBurstEntity entity) {
      if (entity.isSoulFire()) {
         return SOUL_TEXTURE;
      } else {
         return entity.isInfernalFire() ? INFERNAL_TEXTURE : TEXTURE;
      }
   }
}
