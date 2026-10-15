package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.RitualFireSigilEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RitualFireSigilRenderer extends EntityRenderer<RitualFireSigilEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/flame_strike_sigil.png");
   private static final ResourceLocation SOUL_TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/soul_flame_strike_sigil.png");
   private static final ResourceLocation INFERNAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/infernal_flame_strike_sigil.png"
   );

   public RitualFireSigilRenderer(Context context) {
      super(context);
   }

   public void render(RitualFireSigilEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      float progress = entity.getProgress(partialTicks);
      float pulse = (float)((Math.sin((entity.tickCount + partialTicks) * 0.28F) + 1.0) * 0.5);
      float radius = entity.getRadius() * (0.94F + pulse * 0.08F);
      float rotation = (entity.tickCount + partialTicks) * 1.6F;
      poseStack.pushPose();
      poseStack.translate(0.0, 0.02, 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
      Matrix4f pose = poseStack.last().pose();
      Matrix3f normal = poseStack.last().normal();
      int alpha = (int)(255.0F * Math.min(1.0F, 0.25F + (1.0F - progress) * 0.9F));
      vertex(consumer, pose, normal, light, -radius, 0.0F, -radius, 0.0F, 0.0F, alpha);
      vertex(consumer, pose, normal, light, -radius, 0.0F, radius, 0.0F, 1.0F, alpha);
      vertex(consumer, pose, normal, light, radius, 0.0F, radius, 1.0F, 1.0F, alpha);
      vertex(consumer, pose, normal, light, radius, 0.0F, -radius, 1.0F, 0.0F, alpha);
      poseStack.popPose();
      super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, light);
   }

   private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int light, float x, float y, float z, float u, float v, int alpha) {
      consumer.addVertex(pose, x, y, z)
         .setColor(255, 255, 255, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(RitualFireSigilEntity entity) {
      return switch (entity.getMode()) {
         case SOUL_FIRE -> SOUL_TEXTURE;
         case INFERNAL_FIRE -> INFERNAL_TEXTURE;
         default -> TEXTURE;
      };
   }
}
