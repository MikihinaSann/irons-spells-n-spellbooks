package com.gametechbc.traveloptics.entity.spells.gyro_slash;

import com.gametechbc.traveloptics.TravelopticsMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GyroSlashProjectileRenderer extends EntityRenderer<GyroSlashProjectile> {
   private static ResourceLocation[] TEXTURES = new ResourceLocation[]{
      TravelopticsMod.id("textures/entity/gyro_slash_projectile/gyro_slash_projectile.png"),
      TravelopticsMod.id("textures/entity/gyro_slash_projectile/gyro_slash_projectile.png")
   };

   public GyroSlashProjectileRenderer(Context context) {
      super(context);
   }

   public void render(GyroSlashProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      Matrix4f poseMatrix = pose.pose();
      Matrix3f normalMatrix = pose.normal();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
      poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
      float oldWidth = (float)entity.oldBB.getXsize();
      float width = entity.getBbWidth();
      width = oldWidth + (width - oldWidth) * Math.min(partialTicks, 1.0F);
      this.drawSlash(pose, entity, bufferSource, light, width, 0);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   private void drawSlash(Pose pose, GyroSlashProjectile entity, MultiBufferSource bufferSource, int light, float width, int offset) {
      Matrix4f poseMatrix = pose.pose();
      Matrix3f normalMatrix = pose.normal();
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity, offset)));
      float halfWidth = width * 0.5F;
      consumer.addVertex(poseMatrix, -halfWidth, -0.1F, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, -0.1F, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, -0.1F, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, -halfWidth, -0.1F, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(GyroSlashProjectile entity) {
      int frame = entity.animationTime / 4 % TEXTURES.length;
      return TEXTURES[frame];
   }

   private ResourceLocation getTextureLocation(GyroSlashProjectile entity, int offset) {
      int frame = (entity.animationTime / 6 + offset) % TEXTURES.length;
      return TEXTURES[frame];
   }
}
