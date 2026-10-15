package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.WitheredRoseVortexEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WitheredRoseVortexRenderer extends EntityRenderer<WitheredRoseVortexEntity> {
   private static final ResourceLocation BLOOM_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/withered_rose_vortex_bloom.png");
   private static final ResourceLocation RIBBON_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/withered_rose_vortex_ribbon.png"
   );
   private static final ResourceLocation CROWN_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/withered_rose_vortex_crown.png");
   private static final int FULL_BRIGHT = 15728880;
   private static final int RIBBON_SEGMENTS = 36;
   private static final int CROWN_PETALS = 3;

   public WitheredRoseVortexRenderer(Context context) {
      super(context);
   }

   public void render(WitheredRoseVortexEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      float age = entity.tickCount + partialTick;
      float radius = entity.getRadius();
      float warmup = entity.getWarmupProgress(partialTick);
      float collapse = entity.getCollapseProgress(partialTick);
      float active = warmup * (1.0F - collapse);
      float pulse = entity.getPulseStrength(partialTick);
      float breathe = 0.5F + 0.5F * Mth.sin(age * 0.16F);
      renderGroundBloom(poseStack, bufferSource, radius, age, warmup, collapse);
      if (active > 0.01F) {
         renderThornRibbon(poseStack, bufferSource, radius, age, active, collapse, pulse, 0.0F, false);
         renderThornRibbon(poseStack, bufferSource, radius * 0.78F, age, active, collapse, pulse, 180.0F, true);
         renderCrown(poseStack, bufferSource, radius, age, active, collapse, pulse, breathe);
      }

      super.render(entity, yaw, partialTick, poseStack, bufferSource, packedLight);
   }

   private static void renderGroundBloom(PoseStack poseStack, MultiBufferSource bufferSource, float radius, float age, float warmup, float collapse) {
      float open = Mth.clamp(warmup * 1.18F, 0.0F, 1.0F);
      float scale = radius * (0.18F + 0.82F * open);
      float closingScale = Mth.lerp(collapse, 1.0F, 0.18F);
      int alpha = alpha(0.22F + warmup * 0.55F, collapse);
      renderGroundQuad(poseStack, bufferSource, BLOOM_TEXTURE, scale * closingScale, age * 1.15F, 0.022, 255, 255, 255, alpha);
      renderGroundQuad(poseStack, bufferSource, BLOOM_TEXTURE, scale * 0.71F * closingScale, -age * 1.72F, 0.042, 188, 38, 64, Math.max(0, alpha - 55));
   }

   private static void renderGroundQuad(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      ResourceLocation texture,
      float radius,
      float rotation,
      double yOffset,
      int red,
      int green,
      int blue,
      int alpha
   ) {
      if (alpha > 0 && !(radius <= 0.01F)) {
         poseStack.pushPose();
         poseStack.translate(0.0, yOffset, 0.0);
         poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture));
         Matrix4f pose = poseStack.last().pose();
         Matrix3f normal = poseStack.last().normal();
         vertex(consumer, pose, normal, -radius, 0.0F, -radius, 0.0F, 0.0F, red, green, blue, alpha);
         vertex(consumer, pose, normal, -radius, 0.0F, radius, 0.0F, 1.0F, red, green, blue, alpha);
         vertex(consumer, pose, normal, radius, 0.0F, radius, 1.0F, 1.0F, red, green, blue, alpha);
         vertex(consumer, pose, normal, radius, 0.0F, -radius, 1.0F, 0.0F, red, green, blue, alpha);
         poseStack.popPose();
      }
   }

   private static void renderThornRibbon(
      PoseStack poseStack, MultiBufferSource bufferSource, float radius, float age, float active, float collapse, float pulse, float phase, boolean reverse
   ) {
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RIBBON_TEXTURE));
      Matrix4f pose = poseStack.last().pose();
      Matrix3f normal = poseStack.last().normal();
      float height = 6.4F * active * Mth.lerp(collapse, 1.0F, 0.12F);
      float baseWidth = radius * (reverse ? 0.095F : 0.12F) * (1.0F + pulse * 0.26F);
      float rotation = age * (reverse ? -6.2F : 7.3F) + phase;
      float turns = reverse ? -2.25F : 2.6F;
      int alpha = alpha(reverse ? 0.56F : 0.72F, collapse);
      int red = reverse ? 220 : 255;
      int green = reverse ? 62 : 255;
      int blue = reverse ? 82 : 255;
      Vec3 previousCenter = helixPoint(radius, height, 0.0F, rotation, turns, collapse, reverse);
      Vec3 previousSide = ribbonSide(radius, height, 0.0F, rotation, turns, baseWidth, collapse, reverse);

      for (int segment = 1; segment <= 36; segment++) {
         float t = segment / 36.0F;
         Vec3 center = helixPoint(radius, height, t, rotation, turns, collapse, reverse);
         Vec3 side = ribbonSide(radius, height, t, rotation, turns, baseWidth, collapse, reverse);
         float v0 = (segment - 1) / 5.0F + age * (reverse ? -0.025F : 0.032F);
         float v1 = segment / 5.0F + age * (reverse ? -0.025F : 0.032F);
         ribbonQuad(consumer, pose, normal, previousCenter, center, previousSide, side, v0, v1, red, green, blue, alpha);
         previousCenter = center;
         previousSide = side;
      }
   }

   private static Vec3 helixPoint(float radius, float height, float t, float rotation, float turns, float collapse, boolean reverse) {
      float topTaper = Mth.lerp(t, 1.0F, 0.22F);
      float collapseRadius = Mth.lerp(collapse, 1.0F, 0.08F + 0.14F * (1.0F - t));
      float localRadius = radius * topTaper * collapseRadius;
      float angle = (float)Math.toRadians(rotation + 360.0F * turns * t);
      float sway = Mth.sin(t * (float) Math.PI * 3.0F + rotation * 0.025F) * radius * 0.035F;
      return new Vec3(Mth.cos(angle) * (localRadius + sway), height * t, Mth.sin(angle) * (localRadius + sway));
   }

   private static Vec3 ribbonSide(float radius, float height, float t, float rotation, float turns, float width, float collapse, boolean reverse) {
      float epsilon = 0.027777778F;
      Vec3 before = helixPoint(radius, height, Math.max(0.0F, t - epsilon), rotation, turns, collapse, reverse);
      Vec3 after = helixPoint(radius, height, Math.min(1.0F, t + epsilon), rotation, turns, collapse, reverse);
      Vec3 tangent = after.subtract(before).normalize();
      Vec3 radial = new Vec3(before.x, 0.0, before.z);
      if (radial.lengthSqr() < 1.0E-5) {
         radial = new Vec3(1.0, 0.0, 0.0);
      }

      Vec3 side = tangent.cross(radial.normalize());
      if (side.lengthSqr() < 1.0E-5) {
         side = tangent.cross(new Vec3(0.0, 1.0, 0.0));
      }

      float taperedWidth = width * (0.78F + Mth.sin(t * (float) Math.PI) * 0.45F);
      return side.normalize().scale(taperedWidth);
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
   }

   private static void renderCrown(
      PoseStack poseStack, MultiBufferSource bufferSource, float radius, float age, float active, float collapse, float pulse, float breathe
   ) {
      float opening = Mth.clamp((active - 0.18F) / 0.62F, 0.0F, 1.0F);
      float crownScale = radius * (0.3F + opening * 0.17F) * (0.94F + breathe * 0.08F + pulse * 0.12F) * Mth.lerp(collapse, 1.0F, 0.14F);
      float y = 6.4F * active * Mth.lerp(collapse, 1.0F, 0.08F);
      int alpha = alpha(0.72F + pulse * 0.18F, collapse);
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(CROWN_TEXTURE));

      for (int petalPlane = 0; petalPlane < 3; petalPlane++) {
         poseStack.pushPose();
         poseStack.translate(0.0, y, 0.0);
         poseStack.mulPose(Axis.YP.rotationDegrees(age * -3.8F + petalPlane * 120.0F));
         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F - opening * 24.0F + collapse * 68.0F));
         Matrix4f pose = poseStack.last().pose();
         Matrix3f normal = poseStack.last().normal();
         crownQuad(consumer, pose, normal, crownScale, 255, 255, 255, alpha);
         poseStack.popPose();
      }

      poseStack.pushPose();
      poseStack.translate(0.0, y + 0.02, 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(age * 2.7F));
      Matrix4f pose = poseStack.last().pose();
      Matrix3f normal = poseStack.last().normal();
      crownGroundQuad(consumer, pose, normal, crownScale * 0.82F, 255, 255, 255, Math.max(0, alpha - 25));
      poseStack.popPose();
   }

   private static void crownQuad(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float radius, int red, int green, int blue, int alpha) {
      vertex(consumer, pose, normal, -radius, -radius, 0.0F, 0.0F, 1.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, radius, -radius, 0.0F, 1.0F, 1.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, radius, radius, 0.0F, 1.0F, 0.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, -radius, radius, 0.0F, 0.0F, 0.0F, red, green, blue, alpha);
   }

   private static void crownGroundQuad(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float radius, int red, int green, int blue, int alpha) {
      vertex(consumer, pose, normal, -radius, 0.0F, -radius, 0.0F, 0.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, -radius, 0.0F, radius, 0.0F, 1.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, radius, 0.0F, radius, 1.0F, 1.0F, red, green, blue, alpha);
      vertex(consumer, pose, normal, radius, 0.0F, -radius, 1.0F, 0.0F, red, green, blue, alpha);
   }

   private static int alpha(float base, float collapse) {
      return Mth.clamp((int)(255.0F * base * (1.0F - collapse)), 0, 255);
   }

   private static void vertex(
      VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float z, float u, float v, int red, int green, int blue, int alpha
   ) {
      consumer.addVertex(pose, x, y, z)
         .setColor(red, green, blue, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, Vec3 position, float u, float v, int red, int green, int blue, int alpha) {
      vertex(consumer, pose, normal, (float)position.x, (float)position.y, (float)position.z, u, v, red, green, blue, alpha);
   }

   public boolean shouldRender(WitheredRoseVortexEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
      return true;
   }

   public ResourceLocation getTextureLocation(WitheredRoseVortexEntity entity) {
      return BLOOM_TEXTURE;
   }
}
