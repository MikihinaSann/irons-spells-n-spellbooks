package com.somake.somakespells.render;

import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.somake.somakespells.entity.SoulLatchTetherEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class SoulLatchTetherRenderer extends EntityRenderer<SoulLatchTetherEntity> {
   private static final ResourceLocation TETHER_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/particle/ribbon_direct.png");

   public SoulLatchTetherRenderer(Context context) {
      super(context);
   }

   public void render(SoulLatchTetherEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      if (entity.getOwnerEntity() != null && entity.getTargetEntity() != null) {
         Vec3 start = entity.getOwnerAnchor();
         Vec3 end = entity.getTargetAnchor();
         Vec3 entityPos = entity.position();
         Vec3 chainStart = start.subtract(entityPos);
         Vec3 chainVector = end.subtract(start);
         poseStack.pushPose();
         poseStack.translate(chainStart.x, chainStart.y, chainStart.z);
         this.renderSpiritRibbon(chainVector, poseStack, bufferSource, packedLight, entity.isRedSoul());
         poseStack.popPose();
      }
   }

   private void renderSpiritRibbon(Vec3 chainVector, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, boolean redSoul) {
      int sampleSize = 18;
      float trailWidth = 0.12F;
      Pose pose = poseStack.last();
      Matrix4f matrix = pose.pose();
      VertexConsumer consumer = bufferSource.getBuffer(CMRenderTypes.getLightTrailEffect(TETHER_TEXTURE));
      Vec3 drawFrom = Vec3.ZERO;

      for (int i = 0; i < sampleSize; i++) {
         float t1 = (i + 1.0F) / sampleSize;
         Vec3 sample = chainVector.scale(t1);
         Vec3 tangent = sample.subtract(drawFrom);
         if (tangent.lengthSqr() < 1.0E-4) {
            tangent = chainVector;
         }

         Vec3 reference = Math.abs(tangent.normalize().y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
         Vec3 side = tangent.cross(reference);
         if (side.lengthSqr() < 1.0E-4) {
            side = tangent.cross(new Vec3(0.0, 0.0, 1.0));
         }

         side = side.normalize().scale(trailWidth);
         float u1 = (float)i / sampleSize;
         this.addVertex(
            consumer, matrix, drawFrom, side.scale(-1.0), redSoul ? 0.86F : 0.56F, redSoul ? 0.08F : 0.98F, redSoul ? 0.1F : 0.92F, u1, 1.0F, packedLight
         );
         this.addVertex(
            consumer, matrix, sample, side.scale(-1.0), redSoul ? 0.72F : 0.4F, redSoul ? 0.03F : 0.92F, redSoul ? 0.05F : 0.88F, t1, 1.0F, packedLight
         );
         this.addVertex(consumer, matrix, sample, side, redSoul ? 1.0F : 0.82F, redSoul ? 0.34F : 1.0F, redSoul ? 0.3F : 0.96F, t1, 0.0F, packedLight);
         this.addVertex(consumer, matrix, drawFrom, side, redSoul ? 0.92F : 0.64F, redSoul ? 0.14F : 0.96F, redSoul ? 0.12F : 0.9F, u1, 0.0F, packedLight);
         drawFrom = sample;
      }
   }

   private void addVertex(VertexConsumer consumer, Matrix4f matrix, Vec3 pos, Vec3 offset, float r, float g, float b, float u, float v, int light) {
      consumer.addVertex(matrix, (float)(pos.x + offset.x), (float)(pos.y + offset.y), (float)(pos.z + offset.z))
         .setColor(r, g, b, 0.95F)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public boolean shouldRender(SoulLatchTetherEntity entity, Frustum camera, double camX, double camY, double camZ) {
      return true;
   }

   public ResourceLocation getTextureLocation(SoulLatchTetherEntity entity) {
      return TETHER_TEXTURE;
   }
}
