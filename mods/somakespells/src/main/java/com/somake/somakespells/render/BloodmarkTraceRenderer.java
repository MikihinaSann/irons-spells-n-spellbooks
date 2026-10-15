package com.somake.somakespells.render;

import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.somake.somakespells.entity.BloodmarkTraceEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BloodmarkTraceRenderer extends EntityRenderer<BloodmarkTraceEntity> {
   private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/particle/ribbon_direct.png");

   public BloodmarkTraceRenderer(Context context) {
      super(context);
   }

   public void render(BloodmarkTraceEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      Vec3 entityPos = entity.position();
      Vec3 localStart = entity.getStart().subtract(entityPos);
      Vec3 localEnd = entity.getEnd().subtract(entityPos);
      float progress = entity.getBuildProgress(partialTick);
      int sampleSize = 14;
      float trailWidth = 0.18F;
      Pose pose = poseStack.last();
      Matrix4f matrix = pose.pose();
      VertexConsumer consumer = bufferSource.getBuffer(CMRenderTypes.getLightTrailEffect(TRAIL_TEXTURE));
      Vec3 drawFrom = localStart;

      for (int i = 0; i < sampleSize; i++) {
         float t1 = (i + 1.0F) / sampleSize;
         if (t1 > progress) {
            t1 = progress;
         }

         if (!(t1 <= 0.001F)) {
            Vec3 sample = localStart.lerp(localEnd, t1);
            Vec3 tangent = sample.subtract(drawFrom);
            if (tangent.lengthSqr() < 1.0E-4) {
               tangent = localEnd.subtract(localStart);
            }

            Vec3 reference = Math.abs(tangent.normalize().y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
            Vec3 side = tangent.cross(reference);
            if (side.lengthSqr() < 1.0E-4) {
               side = tangent.cross(new Vec3(0.0, 0.0, 1.0));
            }

            side = side.normalize().scale(trailWidth);
            float u1 = (float)i / sampleSize;
            this.addVertex(consumer, matrix, drawFrom, side.scale(-1.0), 0.86F, 0.12F, 0.16F, 0.0F, u1, 1.0F, packedLight);
            this.addVertex(consumer, matrix, sample, side.scale(-1.0), 0.86F, 0.12F, 0.16F, 0.0F, t1, 1.0F, packedLight);
            this.addVertex(consumer, matrix, sample, side, 0.86F, 0.12F, 0.16F, 0.0F, t1, 0.0F, packedLight);
            this.addVertex(consumer, matrix, drawFrom, side, 0.86F, 0.12F, 0.16F, 0.0F, u1, 0.0F, packedLight);
            drawFrom = sample;
            if (t1 >= progress) {
               break;
            }
         }
      }

      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   private void addVertex(VertexConsumer consumer, Matrix4f matrix, Vec3 pos, Vec3 offset, float r, float g, float b, float a, float u, float v, int light) {
      consumer.addVertex(matrix, (float)(pos.x + offset.x), (float)(pos.y + offset.y), (float)(pos.z + offset.z))
         .setColor(r, g, b, 0.92F)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(BloodmarkTraceEntity entity) {
      return TRAIL_TEXTURE;
   }

   public boolean shouldRender(BloodmarkTraceEntity entity, Frustum camera, double camX, double camY, double camZ) {
      return true;
   }
}
