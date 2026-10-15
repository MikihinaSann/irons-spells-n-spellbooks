package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.ElementalChargeBeamEntity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import io.redspace.ironsspellbooks.render.RenderHelper.CustomerRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ElementalChargeBeamRenderer extends EntityRenderer<ElementalChargeBeamEntity> {
   private static final float MAX_WIDTH = 2.5F;
   private static final float MIN_WIDTH = 0.005F;
   private static final float BEAM_HEIGHT = 250.0F;

   public ElementalChargeBeamRenderer(Context context) {
      super(context);
   }

   public boolean shouldRender(ElementalChargeBeamEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
      return true;
   }

   public void render(ElementalChargeBeamEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      float age = entity.tickCount + partialTick;
      float deltaUv = -age % 10.0F;
      float maxV = Mth.frac(deltaUv * 0.2F - Mth.floor(deltaUv * 0.1F));
      float minV = maxV - 1.0F;
      float progress = Mth.clamp(age / 15.0F, 0.0F, 1.0F);
      float easedProgress = progress * progress;
      float width = Mth.clampedLerp(2.5F, 0.005F, easedProgress);
      float outerHalfWidth = width * 0.5F;
      float innerHalfWidth = outerHalfWidth * 0.5F;
      float yMin = entity.onGround() ? 0.0F : Utils.findRelativeGroundLevel(entity.level(), entity.position(), 8) - (float)entity.getY();
      int color = entity.getColor();
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      float outerIntensity = Mth.clamp(0.85F * easedProgress, 0.0F, 1.0F);
      float innerIntensity = Mth.clamp(easedProgress, 0.0F, 1.0F);
      VertexConsumer consumer = bufferSource.getBuffer(CustomerRenderType.magic(SpellRenderingHelper.BEACON));

      for (int face = 0; face < 4; face++) {
         drawFace(poseStack, consumer, outerHalfWidth, yMin, minV, maxV, red * outerIntensity, green * outerIntensity, blue * outerIntensity);
         drawFace(
            poseStack,
            consumer,
            innerHalfWidth,
            yMin,
            minV,
            maxV,
            brighten(red) * innerIntensity,
            brighten(green) * innerIntensity,
            brighten(blue) * innerIntensity
         );
         poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      }

      poseStack.popPose();
      super.render(entity, yaw, partialTick, poseStack, bufferSource, packedLight);
   }

   private static float brighten(float channel) {
      return Mth.lerp(0.28F, channel, 1.0F);
   }

   private static void drawFace(
      PoseStack poseStack, VertexConsumer consumer, float halfWidth, float yMin, float minV, float maxV, float red, float green, float blue
   ) {
      RenderHelper.quadBuilder()
         .vertex(-halfWidth, yMin, -halfWidth)
         .uv(0.0F, minV)
         .normal(0.0F, 1.0F, 0.0F)
         .vertex(-halfWidth, yMin, halfWidth)
         .uv(1.0F, minV)
         .normal(0.0F, 1.0F, 0.0F)
         .vertex(-halfWidth, 250.0F, halfWidth)
         .uv(1.0F, maxV)
         .normal(0.0F, 1.0F, 0.0F)
         .vertex(-halfWidth, 250.0F, -halfWidth)
         .uv(0.0F, maxV)
         .normal(0.0F, 1.0F, 0.0F)
         .color(red, green, blue)
         .light(15728880)
         .overlay(OverlayTexture.NO_OVERLAY)
         .matrix(poseStack.last().pose())
         .build(consumer);
   }

   public ResourceLocation getTextureLocation(ElementalChargeBeamEntity entity) {
      return SpellRenderingHelper.BEACON;
   }
}
