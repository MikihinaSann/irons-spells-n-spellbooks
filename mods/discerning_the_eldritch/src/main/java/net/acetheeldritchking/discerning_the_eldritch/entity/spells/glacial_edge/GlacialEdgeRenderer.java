package net.acetheeldritchking.discerning_the_eldritch.entity.spells.glacial_edge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.Random;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class GlacialEdgeRenderer extends EntityRenderer<GlacialEdge> {
   private static final ResourceLocation TEX = DiscerningTheEldritch.id("textures/entity/glacial_edge/glacial_edge.png");

   public GlacialEdgeRenderer(Context context) {
      super(context);
   }

   public void render(GlacialEdge glacialEdge, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, glacialEdge.yRotO, glacialEdge.getYRot())));
      poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTick, glacialEdge.xRotO, glacialEdge.getXRot())));
      float randomZ = new Random(31L * glacialEdge.getId()).nextInt(-8, 8);
      poseStack.mulPose(Axis.XP.rotationDegrees(randomZ));
      this.createSlashTexturePlace(pose, glacialEdge, bufferSource, glacialEdge.getBbWidth() * 1.5F);
      poseStack.popPose();
      super.render(glacialEdge, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   private void createSlashTexturePlace(Pose pose, GlacialEdge glacialEdge, MultiBufferSource bufferSource, float width) {
      Matrix4f poseMatrix = pose.pose();
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(glacialEdge)));
      float halfWidth = width * 0.5F;
      float height = glacialEdge.getBbHeight() * 0.5F;
      consumer.addVertex(poseMatrix, -halfWidth, height, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, height, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, height, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, -halfWidth, height, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(GlacialEdge glacialEdge) {
      return TEX;
   }
}
