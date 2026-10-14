package com.gametechbc.traveloptics.entity.spells.reversal;

import com.gametechbc.traveloptics.TravelopticsMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ReversalRenderer extends EntityRenderer<ReversalEntity> {
   private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
      TravelopticsMod.id("textures/entity/reversal/reversal_1.png"),
      TravelopticsMod.id("textures/entity/reversal/reversal_2.png"),
      TravelopticsMod.id("textures/entity/reversal/reversal_3.png"),
      TravelopticsMod.id("textures/entity/reversal/reversal_4.png")
   };

   public ReversalRenderer(Context context) {
      super(context);
   }

   public void render(ReversalEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - entity.getYRot()));
      poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
      float randomZ = new Random(31L * entity.getId()).nextInt(-8, 8);
      poseStack.mulPose(Axis.XP.rotationDegrees(randomZ));
      this.createTexturePlane(pose, entity, bufferSource, entity.getBbWidth() * 1.5F, entity.isMirrored());
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   private void createTexturePlane(Pose pose, ReversalEntity entity, MultiBufferSource bufferSource, float width, boolean mirrored) {
      Matrix4f poseMatrix = pose.pose();
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
      float halfWidth = width * 0.5F;
      float height = entity.getBbHeight() * 0.5F;
      consumer.addVertex(poseMatrix, -halfWidth, height, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, mirrored ? 0.0F : 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, height, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, mirrored ? 0.0F : 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, height, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, mirrored ? 1.0F : 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, -halfWidth, height, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, mirrored ? 1.0F : 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(ReversalEntity entity) {
      int frame = entity.tickCount / 2 % TEXTURES.length;
      return TEXTURES[frame];
   }
}
