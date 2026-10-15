package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.FuneralBloomControllerEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FuneralBloomControllerRenderer extends EntityRenderer<FuneralBloomControllerEntity> {
   private static final ResourceLocation BLOOM = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/withered_rose_vortex_bloom.png");
   private static final ResourceLocation CROWN = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/withered_rose_vortex_crown.png");
   private static final ResourceLocation RIBBON = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/withered_rose_vortex_ribbon.png");

   public FuneralBloomControllerRenderer(Context context) {
      super(context);
   }

   public void render(FuneralBloomControllerEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
      float age = entity.tickCount + partialTick;
      float warmup = entity.getWarmupProgress(partialTick);
      float collapse = entity.getCollapseProgress(partialTick);
      float visible = warmup * (1.0F - collapse);
      float pulse = entity.getWavePulse(partialTick);
      float radius = entity.getRadius();
      if (visible > 0.01F) {
         renderSkyBloom(poseStack, buffers, radius, age, visible, collapse, pulse);
         renderConnectingVines(poseStack, buffers, radius, age, visible, collapse, pulse);
      }

      super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
   }

   private static void renderSkyBloom(PoseStack poseStack, MultiBufferSource buffers, float radius, float age, float visible, float collapse, float pulse) {
      float y = 10.5F * visible * Mth.lerp(collapse, 1.0F, 0.08F);
      float scale = radius * (0.42F + visible * 0.23F) * (1.0F + pulse * 0.07F) * Mth.lerp(collapse, 1.0F, 0.12F);
      int alpha = alpha(0.68F + pulse * 0.12F, collapse);
      poseStack.pushPose();
      poseStack.translate(0.0, y, 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(-age * 1.16F));
      FuneralBloomRenderUtil.groundQuad(poseStack, buffers, BLOOM, scale, 0.0, 255, 255, 255, alpha, true);
      poseStack.mulPose(Axis.YP.rotationDegrees(age * 2.25F));
      FuneralBloomRenderUtil.groundQuad(poseStack, buffers, CROWN, scale * 0.54F, -0.035, 214, 74, 102, Math.max(0, alpha - 12), true);
      poseStack.popPose();
   }

   private static void renderConnectingVines(
      PoseStack poseStack, MultiBufferSource buffers, float radius, float age, float visible, float collapse, float pulse
   ) {
      float height = 10.5F * visible * Mth.lerp(collapse, 1.0F, 0.06F);
      int alpha = alpha(0.52F, collapse);

      for (int vine = 0; vine < 4; vine++) {
         List<Vec3> points = new ArrayList<>();
         float phase = age * (vine % 2 == 0 ? 1.55F : -1.28F) + vine * 90.0F;

         for (int segment = 0; segment <= 18; segment++) {
            float t = segment / 18.0F;
            float arch = Mth.sin(t * (float) Math.PI);
            float localRadius = radius * Mth.lerp(t, 0.78F, 0.48F) * (1.0F - collapse * 0.86F);
            float angle = (float)Math.toRadians(phase + t * (vine % 2 == 0 ? 105.0F : -105.0F));
            float vertical = height * t + arch * 0.48F;
            points.add(new Vec3(Mth.cos(angle) * localRadius, vertical, Mth.sin(angle) * localRadius));
         }

         FuneralBloomRenderUtil.ribbon(
            poseStack,
            buffers,
            RIBBON,
            points,
            radius * (0.038F + pulse * 0.006F),
            age * 0.018F + vine * 0.37F,
            vine % 2 == 0 ? 255 : 205,
            vine % 2 == 0 ? 255 : 46,
            vine % 2 == 0 ? 255 : 72,
            alpha
         );
      }
   }

   private static int alpha(float base, float collapse) {
      return Mth.clamp((int)(base * (1.0F - collapse) * 255.0F), 0, 255);
   }

   public boolean shouldRender(FuneralBloomControllerEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
      return true;
   }

   public ResourceLocation getTextureLocation(FuneralBloomControllerEntity entity) {
      return BLOOM;
   }
}
