package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.LightningLanceDanceProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class LightningLanceDanceRenderer extends EntityRenderer<LightningLanceDanceProjectile> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "irons_spellbooks", "textures/entity/lightning_lance/lightning_lance.png"
   );

   public LightningLanceDanceRenderer(Context context) {
      super(context);
   }

   public void render(LightningLanceDanceProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Vec3 motion = entity.getDeltaMovement();
      float xRot;
      float yRot;
      if (motion.lengthSqr() < 1.0E-4) {
         float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
         xRot = pitch;
         yRot = 180.0F - yaw;
      } else {
         xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
         yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      }

      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      renderModel(poseStack, bufferSource, entity.tickCount);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
      Pose pose = poseStack.last();
      Matrix4f poseMatrix = pose.pose();
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(TEXTURE, 0.0F, 0.0F));
      int framecout = 7;
      int anim = animOffset % framecout;
      float uvMin = (float)anim / framecout;
      float uvMax = (float)(anim + 1) / framecout;
      float halfWidth = 2.0F;
      float halfHeight = 1.0F;
      float angleCorrection = 50.0F;
      float texturefix = -0.2F;
      poseStack.mulPose(Axis.XP.rotationDegrees(angleCorrection));
      consumer.addVertex(poseMatrix, 0.0F, -halfWidth, -halfHeight + texturefix)
         .setColor(255, 255, 255, 255)
         .setUv(uvMin, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, 0.0F, halfWidth, -halfHeight + texturefix)
         .setColor(255, 255, 255, 255)
         .setUv(uvMin, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, 0.0F, halfWidth, halfHeight + texturefix)
         .setColor(255, 255, 255, 255)
         .setUv(uvMax, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, 0.0F, -halfWidth, halfHeight + texturefix)
         .setColor(255, 255, 255, 255)
         .setUv(uvMax, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      poseStack.mulPose(Axis.XP.rotationDegrees(-angleCorrection));
      poseStack.mulPose(Axis.YP.rotationDegrees(-angleCorrection));
      consumer.addVertex(poseMatrix, -halfWidth - texturefix, 0.0F, -halfHeight)
         .setColor(255, 255, 255, 255)
         .setUv(uvMin, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth - texturefix, 0.0F, -halfHeight)
         .setColor(255, 255, 255, 255)
         .setUv(uvMin, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth - texturefix, 0.0F, halfHeight)
         .setColor(255, 255, 255, 255)
         .setUv(uvMax, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, -halfWidth - texturefix, 0.0F, halfHeight)
         .setColor(255, 255, 255, 255)
         .setUv(uvMax, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(angleCorrection));
   }

   public ResourceLocation getTextureLocation(LightningLanceDanceProjectile entity) {
      return TEXTURE;
   }
}
