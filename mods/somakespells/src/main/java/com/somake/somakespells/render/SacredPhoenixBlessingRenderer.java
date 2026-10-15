package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.SacredPhoenixBlessingControllerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SacredPhoenixBlessingRenderer extends EntityRenderer<SacredPhoenixBlessingControllerEntity> {
   private static final ResourceLocation AURA_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/sacred_phoenix_blessing_aura.png"
   );
   private static final ResourceLocation PILLAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/sacred_phoenix_blessing_pillar.png"
   );

   public SacredPhoenixBlessingRenderer(Context context) {
      super(context);
   }

   public void render(
      SacredPhoenixBlessingControllerEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light
   ) {
      float age = entity.tickCount + partialTick;
      float pulse = (float)((Math.sin(age * 0.22F) + 1.0) * 0.5);
      float radius = Math.max(4.0F, entity.getRadius());
      float pulseAge = age >= 12.0F ? (age - 12.0F) % 24.0F : 24.0F;
      float pillarStrength = Math.max(0.0F, 1.0F - pulseAge / 9.0F);
      renderAura(poseStack, bufferSource, radius * (0.96F + pulse * 0.06F), age * 1.8F, 190);
      renderAura(poseStack, bufferSource, radius * 0.72F, -age * 2.6F, 120);
      if (pillarStrength > 0.0F) {
         renderPillar(poseStack, bufferSource, radius * (0.22F + pillarStrength * 0.3F), 3.0F + pillarStrength * 11.0F, 35 + (int)(pillarStrength * 155.0F));
      }

      super.render(entity, yaw, partialTick, poseStack, bufferSource, light);
   }

   private static void renderAura(PoseStack poseStack, MultiBufferSource source, float radius, float rotation, int alpha) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.035, 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      VertexConsumer consumer = source.getBuffer(RenderType.entityTranslucentEmissive(AURA_TEXTURE));
      Matrix4f pose = poseStack.last().pose();
      Matrix3f normal = poseStack.last().normal();
      vertex(consumer, pose, normal, -radius, 0.0F, -radius, 0.0F, 0.0F, alpha);
      vertex(consumer, pose, normal, -radius, 0.0F, radius, 0.0F, 1.0F, alpha);
      vertex(consumer, pose, normal, radius, 0.0F, radius, 1.0F, 1.0F, alpha);
      vertex(consumer, pose, normal, radius, 0.0F, -radius, 1.0F, 0.0F, alpha);
      poseStack.popPose();
   }

   private static void renderPillar(PoseStack poseStack, MultiBufferSource source, float halfWidth, float height, int alpha) {
      VertexConsumer consumer = source.getBuffer(RenderType.entityTranslucentEmissive(PILLAR_TEXTURE));

      for (int face = 0; face < 4; face++) {
         Matrix4f pose = poseStack.last().pose();
         Matrix3f normal = poseStack.last().normal();
         vertex(consumer, pose, normal, -halfWidth, 0.0F, -halfWidth, 0.0F, 1.0F, alpha);
         vertex(consumer, pose, normal, -halfWidth, 0.0F, halfWidth, 1.0F, 1.0F, alpha);
         vertex(consumer, pose, normal, -halfWidth, height, halfWidth, 1.0F, 0.0F, 0);
         vertex(consumer, pose, normal, -halfWidth, height, -halfWidth, 0.0F, 0.0F, 0);
         poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      }
   }

   private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float z, float u, float v, int alpha) {
      consumer.addVertex(pose, x, y, z)
         .setColor(255, 30, 48, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(SacredPhoenixBlessingControllerEntity entity) {
      return AURA_TEXTURE;
   }
}
