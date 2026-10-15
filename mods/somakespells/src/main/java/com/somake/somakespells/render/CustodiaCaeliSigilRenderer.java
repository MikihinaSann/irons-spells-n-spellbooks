package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.CustodiaCaeliSigilEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CustodiaCaeliSigilRenderer extends EntityRenderer<CustodiaCaeliSigilEntity> {
   private static final ResourceLocation LARGE_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/custodia_caeli_sigil_0.png");
   private static final ResourceLocation SMALL_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/custodia_caeli_sigil.png");
   private static final int MAIN_R = 255;
   private static final int MAIN_G = 236;
   private static final int MAIN_B = 172;
   private static final int OUTER_R = 255;
   private static final int OUTER_G = 246;
   private static final int OUTER_B = 220;

   public CustodiaCaeliSigilRenderer(Context context) {
      super(context);
   }

   public void render(CustodiaCaeliSigilEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      float progress = entity.getProgress(partialTicks);
      float pulse = (float)((Math.sin((entity.tickCount + partialTicks) * 0.28F) + 1.0) * 0.5);
      float radius = entity.getRadius() * (0.96F + pulse * 0.08F);
      float mainRotation = (entity.tickCount + partialTicks) * 1.8F;
      float counterRotation = -(entity.tickCount + partialTicks) * 1.25F;
      int mainAlpha = (int)(255.0F * Math.min(1.0F, 0.42F + (1.0F - progress) * 0.78F));
      int outerAlpha = (int)(255.0F * Math.min(1.0F, 0.22F + (1.0F - progress) * 0.4F));
      int innerAlpha = (int)(255.0F * Math.min(1.0F, 0.28F + (1.0F - progress) * 0.55F));
      this.renderLayer(poseStack, bufferSource, light, entity, LARGE_TEXTURE, radius * 1.14F, counterRotation, 0.018, outerAlpha, 255, 246, 220);
      this.renderLayer(poseStack, bufferSource, light, entity, LARGE_TEXTURE, radius, mainRotation, 0.026, mainAlpha, 255, 236, 172);
      this.renderLayer(poseStack, bufferSource, light, entity, SMALL_TEXTURE, radius * 0.76F, counterRotation * 1.4F, 0.034, innerAlpha, 255, 255, 245);
      super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, light);
   }

   private void renderLayer(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      CustodiaCaeliSigilEntity entity,
      ResourceLocation texture,
      float radius,
      float rotation,
      double yOffset,
      int alpha,
      int red,
      int green,
      int blue
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0, yOffset, 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture));
      Matrix4f pose = poseStack.last().pose();
      Matrix3f normal = poseStack.last().normal();
      vertex(consumer, pose, normal, light, -radius, 0.0F, -radius, 0.0F, 0.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, light, -radius, 0.0F, radius, 0.0F, 1.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, light, radius, 0.0F, radius, 1.0F, 1.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, light, radius, 0.0F, -radius, 1.0F, 0.0F, red, green, blue, alpha);
      poseStack.popPose();
   }

   private static void vertex(
      VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int light, float x, float y, float z, float u, float v, int red, int green, int blue, int alpha
   ) {
      consumer.addVertex(pose, x, y, z)
         .setColor(red, green, blue, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(CustodiaCaeliSigilEntity entity) {
      return LARGE_TEXTURE;
   }
}
