package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

final class FuneralBloomRenderUtil {
   private FuneralBloomRenderUtil() {
   }

   static void groundQuad(
      PoseStack poseStack,
      MultiBufferSource buffers,
      ResourceLocation texture,
      float radius,
      double yOffset,
      int red,
      int green,
      int blue,
      int alpha,
      boolean doubleSided
   ) {
      if (!(radius <= 0.001F) && alpha > 0) {
         poseStack.pushPose();
         poseStack.translate(0.0, yOffset, 0.0);
         VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentEmissive(texture));
         Matrix4f pose = poseStack.last().pose();
         Matrix3f normal = poseStack.last().normal();
         vertex(consumer, pose, normal, -radius, 0.0F, -radius, 0.0F, 0.0F, red, green, blue, alpha, 0.0F, 1.0F, 0.0F);
         vertex(consumer, pose, normal, -radius, 0.0F, radius, 0.0F, 1.0F, red, green, blue, alpha, 0.0F, 1.0F, 0.0F);
         vertex(consumer, pose, normal, radius, 0.0F, radius, 1.0F, 1.0F, red, green, blue, alpha, 0.0F, 1.0F, 0.0F);
         vertex(consumer, pose, normal, radius, 0.0F, -radius, 1.0F, 0.0F, red, green, blue, alpha, 0.0F, 1.0F, 0.0F);
         if (doubleSided) {
            vertex(consumer, pose, normal, radius, 0.0F, -radius, 1.0F, 0.0F, red, green, blue, alpha, 0.0F, -1.0F, 0.0F);
            vertex(consumer, pose, normal, radius, 0.0F, radius, 1.0F, 1.0F, red, green, blue, alpha, 0.0F, -1.0F, 0.0F);
            vertex(consumer, pose, normal, -radius, 0.0F, radius, 0.0F, 1.0F, red, green, blue, alpha, 0.0F, -1.0F, 0.0F);
            vertex(consumer, pose, normal, -radius, 0.0F, -radius, 0.0F, 0.0F, red, green, blue, alpha, 0.0F, -1.0F, 0.0F);
         }

         poseStack.popPose();
      }
   }

   static void crossedBillboard(
      PoseStack poseStack,
      MultiBufferSource buffers,
      ResourceLocation texture,
      float halfWidth,
      float height,
      int planes,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      if (!(halfWidth <= 0.001F) && !(height <= 0.001F) && alpha > 0) {
         VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentEmissive(texture));
         int planeCount = Math.max(2, planes);

         for (int plane = 0; plane < planeCount; plane++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F * plane / planeCount));
            Matrix4f pose = poseStack.last().pose();
            Matrix3f normal = poseStack.last().normal();
            vertex(consumer, pose, normal, -halfWidth, 0.0F, 0.0F, 0.0F, 1.0F, red, green, blue, alpha, 0.0F, 0.0F, 1.0F);
            vertex(consumer, pose, normal, halfWidth, 0.0F, 0.0F, 1.0F, 1.0F, red, green, blue, alpha, 0.0F, 0.0F, 1.0F);
            vertex(consumer, pose, normal, halfWidth, height, 0.0F, 1.0F, 0.0F, red, green, blue, alpha, 0.0F, 0.0F, 1.0F);
            vertex(consumer, pose, normal, -halfWidth, height, 0.0F, 0.0F, 0.0F, red, green, blue, alpha, 0.0F, 0.0F, 1.0F);
            vertex(consumer, pose, normal, -halfWidth, height, 0.0F, 0.0F, 0.0F, red, green, blue, alpha, 0.0F, 0.0F, -1.0F);
            vertex(consumer, pose, normal, halfWidth, height, 0.0F, 1.0F, 0.0F, red, green, blue, alpha, 0.0F, 0.0F, -1.0F);
            vertex(consumer, pose, normal, halfWidth, 0.0F, 0.0F, 1.0F, 1.0F, red, green, blue, alpha, 0.0F, 0.0F, -1.0F);
            vertex(consumer, pose, normal, -halfWidth, 0.0F, 0.0F, 0.0F, 1.0F, red, green, blue, alpha, 0.0F, 0.0F, -1.0F);
            poseStack.popPose();
         }
      }
   }

   static void ribbon(
      PoseStack poseStack,
      MultiBufferSource buffers,
      ResourceLocation texture,
      List<Vec3> points,
      float width,
      float vOffset,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      if (points.size() >= 2 && !(width <= 0.001F) && alpha > 0) {
         VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentEmissive(texture));
         Matrix4f pose = poseStack.last().pose();
         Matrix3f normal = poseStack.last().normal();
         Vec3 previous = points.get(0);
         Vec3 previousSide = side(points, 0, width);
         float previousV = vOffset;

         for (int i = 1; i < points.size(); i++) {
            Vec3 current = points.get(i);
            Vec3 currentSide = side(points, i, width);
            float currentV = vOffset + i / 4.0F;
            ribbonQuad(consumer, pose, normal, previous, current, previousSide, currentSide, previousV, currentV, red, green, blue, alpha);
            previous = current;
            previousSide = currentSide;
            previousV = currentV;
         }
      }
   }

   private static Vec3 side(List<Vec3> points, int index, float width) {
      Vec3 before = points.get(Math.max(0, index - 1));
      Vec3 after = points.get(Math.min(points.size() - 1, index + 1));
      Vec3 tangent = after.subtract(before);
      if (tangent.lengthSqr() < 1.0E-6) {
         tangent = new Vec3(0.0, 1.0, 0.0);
      }

      tangent = tangent.normalize();
      Vec3 radial = points.get(index).multiply(1.0, 0.0, 1.0);
      Vec3 side = radial.lengthSqr() > 1.0E-5 ? tangent.cross(radial.normalize()) : tangent.cross(new Vec3(1.0, 0.0, 0.0));
      if (side.lengthSqr() < 1.0E-5) {
         side = tangent.cross(new Vec3(0.0, 0.0, 1.0));
      }

      return side.normalize().scale(width);
   }

   private static void ribbonQuad(
      VertexConsumer consumer,
      Matrix4f pose,
      Matrix3f normal,
      Vec3 from,
      Vec3 to,
      Vec3 fromSide,
      Vec3 toSide,
      float v0,
      float v1,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      vertex(consumer, pose, normal, from.subtract(fromSide), 0.0F, v0, red, green, blue, alpha);
      vertex(consumer, pose, normal, to.subtract(toSide), 0.0F, v1, red, green, blue, alpha);
      vertex(consumer, pose, normal, to.add(toSide), 1.0F, v1, red, green, blue, alpha);
      vertex(consumer, pose, normal, from.add(fromSide), 1.0F, v0, red, green, blue, alpha);
      vertex(consumer, pose, normal, from.add(fromSide), 1.0F, v0, red, green, blue, alpha);
      vertex(consumer, pose, normal, to.add(toSide), 1.0F, v1, red, green, blue, alpha);
      vertex(consumer, pose, normal, to.subtract(toSide), 0.0F, v1, red, green, blue, alpha);
      vertex(consumer, pose, normal, from.subtract(fromSide), 0.0F, v0, red, green, blue, alpha);
   }

   static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, Vec3 position, float u, float v, int red, int green, int blue, int alpha) {
      Vec3 n = new Vec3(0.0, 1.0, 0.0);
      vertex(consumer, pose, normal, (float)position.x, (float)position.y, (float)position.z, u, v, red, green, blue, alpha, (float)n.x, (float)n.y, (float)n.z);
   }

   static void vertex(
      VertexConsumer consumer,
      Matrix4f pose,
      Matrix3f normal,
      float x,
      float y,
      float z,
      float u,
      float v,
      int red,
      int green,
      int blue,
      int alpha,
      float normalX,
      float normalY,
      float normalZ
   ) {
      consumer.addVertex(pose, x, y, z)
         .setColor(red, green, blue, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(normalX, normalY, normalZ);
   }
}
