package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.FuneralBloomBudEntity;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FuneralBloomBudRenderer extends EntityRenderer<FuneralBloomBudEntity> {
   private static final ResourceLocation BUD = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/funeral_bloom_bud.png");
   private static final ResourceLocation TELEGRAPH = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/funeral_bloom_target_circle.png");
   private static final int TELEGRAPH_SEGMENTS = 48;

   public FuneralBloomBudRenderer(Context context) {
      super(context);
   }

   public void render(FuneralBloomBudEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
      float age = entity.tickCount + partialTick;
      float telegraph = entity.getTelegraphProgress(partialTick);
      float fall = entity.getFallProgress(partialTick);
      float retreat = entity.getRetreatProgress(partialTick);
      Vec3 renderedPosition = new Vec3(
         Mth.lerp(partialTick, entity.xo, entity.getX()), Mth.lerp(partialTick, entity.yo, entity.getY()), Mth.lerp(partialTick, entity.zo, entity.getZ())
      );
      Vec3 impact = entity.getImpactPosition().subtract(renderedPosition);
      float telegraphScale = 0.45F + telegraph * 1.3F;
      int telegraphAlpha = Mth.clamp((int)((0.28F + telegraph * 0.55F) * (1.0F - fall) * 255.0F), 0, 255);
      poseStack.pushPose();
      poseStack.translate(impact.x, impact.y + 0.025, impact.z);
      renderTargetCircle(poseStack, buffers, telegraphScale, telegraph, telegraphAlpha, age);
      renderTargetCircle(poseStack, buffers, telegraphScale * 0.82F, telegraph, Math.max(0, telegraphAlpha - 95), -age * 0.45F);
      poseStack.popPose();
      float budScale = (0.72F + telegraph * 0.3F + fall * 0.16F) * (1.0F - retreat * 0.78F);
      int budAlpha = Mth.clamp((int)((1.0F - retreat) * 235.0F), 0, 255);
      poseStack.pushPose();
      if (fall > 0.001F) {
         Vec3 direction = entity.getImpactPosition().subtract(entity.position());
         if (direction.lengthSqr() > 1.0E-6) {
            direction = direction.normalize();
            Quaternionf targetRotation = new Quaternionf()
               .rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f((float)direction.x, (float)direction.y, (float)direction.z));
            float alignment = Mth.clamp(fall * 3.5F, 0.0F, 1.0F);
            poseStack.mulPose(new Quaternionf().slerp(targetRotation, alignment));
         }
      }

      poseStack.mulPose(Axis.YP.rotationDegrees(age * 8.5F));
      FuneralBloomRenderUtil.crossedBillboard(poseStack, buffers, BUD, 0.82F * budScale, 1.28F * budScale, 3, 255, 255, 255, budAlpha);
      poseStack.popPose();
      super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
   }

   private static void renderTargetCircle(PoseStack poseStack, MultiBufferSource buffers, float radius, float progress, int alpha, float age) {
      if (alpha > 0 && !(radius <= 0.01F)) {
         float thickness = 0.065F + progress * 0.035F;
         float rotation = age * -1.7F;
         int red = Mth.lerpInt(progress, 126, 225);
         int green = Mth.lerpInt(progress, 65, 28);
         int blue = Mth.lerpInt(progress, 81, 58);
         VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentEmissive(TELEGRAPH));
         Matrix4f pose = poseStack.last().pose();
         Matrix3f normal = poseStack.last().normal();

         for (int segment = 0; segment < 48; segment++) {
            float a0 = (float)((float) (Math.PI * 2) * segment / 48.0F + Math.toRadians(rotation));
            float a1 = (float)((float) (Math.PI * 2) * (segment + 1) / 48.0F + Math.toRadians(rotation));
            float inner = radius - thickness;
            float outer = radius + thickness;
            float v0 = segment / 48.0F;
            float v1 = (segment + 1) / 48.0F;
            addCircleVertex(consumer, pose, normal, Mth.cos(a0) * inner, Mth.sin(a0) * inner, 0.0F, v0, red, green, blue, alpha);
            addCircleVertex(consumer, pose, normal, Mth.cos(a1) * inner, Mth.sin(a1) * inner, 0.0F, v1, red, green, blue, alpha);
            addCircleVertex(consumer, pose, normal, Mth.cos(a1) * outer, Mth.sin(a1) * outer, 1.0F, v1, red, green, blue, alpha);
            addCircleVertex(consumer, pose, normal, Mth.cos(a0) * outer, Mth.sin(a0) * outer, 1.0F, v0, red, green, blue, alpha);
         }
      }
   }

   private static void addCircleVertex(
      VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float z, float u, float v, int red, int green, int blue, int alpha
   ) {
      consumer.addVertex(pose, x, 0.0F, z)
         .setColor(red, green, blue, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public boolean shouldRender(FuneralBloomBudEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
      return true;
   }

   public ResourceLocation getTextureLocation(FuneralBloomBudEntity entity) {
      return BUD;
   }
}
