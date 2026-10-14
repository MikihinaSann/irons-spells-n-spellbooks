package com.gametechbc.traveloptics.entity.spells.gyro_slash;

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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GyroSlashVisualRenderer extends EntityRenderer<GyroSlashVisual> {
   private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
      TravelopticsMod.id("textures/entity/gyro_slash_visual/gyro_slash_visual_1.png"),
      TravelopticsMod.id("textures/entity/gyro_slash_visual/gyro_slash_visual_2.png"),
      TravelopticsMod.id("textures/entity/gyro_slash_visual/gyro_slash_visual_3.png"),
      TravelopticsMod.id("textures/entity/gyro_slash_visual/gyro_slash_visual_4.png")
   };

   public GyroSlashVisualRenderer(Context context) {
      super(context);
   }

   public void render(GyroSlashVisual entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - entity.getYRot()));
      poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
      float randomZ = new Random(31L * entity.getId()).nextInt(-8, 8);
      poseStack.mulPose(Axis.XP.rotationDegrees(randomZ));
      this.drawSlash(pose, entity, bufferSource, entity.getBbWidth() * 1.5F, entity.isMirrored());
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   private void drawSlash(Pose pose, GyroSlashVisual entity, MultiBufferSource bufferSource, float width, boolean mirrored) {
      Matrix4f poseMatrix = pose.pose();
      Matrix3f normalMatrix = pose.normal();
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

   public ResourceLocation getTextureLocation(GyroSlashVisual entity) {
      int frame = entity.tickCount / 2 % TEXTURES.length;
      return TEXTURES[frame];
   }
}
