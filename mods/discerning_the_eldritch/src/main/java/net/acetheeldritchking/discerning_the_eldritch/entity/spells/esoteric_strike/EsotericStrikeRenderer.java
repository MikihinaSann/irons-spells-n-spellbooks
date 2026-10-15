package net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_strike;

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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EsotericStrikeRenderer extends EntityRenderer<EsotericStrike> {
   private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
      DiscerningTheEldritch.id("textures/entity/esoteric_strike/esoteric_strike_1.png"),
      DiscerningTheEldritch.id("textures/entity/esoteric_strike/esoteric_strike_2.png"),
      DiscerningTheEldritch.id("textures/entity/esoteric_strike/esoteric_strike_3.png"),
      DiscerningTheEldritch.id("textures/entity/esoteric_strike/esoteric_strike_4.png")
   };

   public EsotericStrikeRenderer(Context context) {
      super(context);
   }

   public void render(EsotericStrike esotericEdge, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - esotericEdge.getYRot()));
      poseStack.mulPose(Axis.ZP.rotationDegrees(esotericEdge.getXRot()));
      float randomZ = new Random(31L * esotericEdge.getId()).nextInt(-5, 5);
      poseStack.mulPose(Axis.XP.rotationDegrees(randomZ));
      this.drawSlash(pose, esotericEdge, bufferSource, esotericEdge.getBbWidth() * 1.5F, esotericEdge.isMirrored());
      poseStack.popPose();
      super.render(esotericEdge, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   private void drawSlash(Pose pose, EsotericStrike entity, MultiBufferSource bufferSource, float width, boolean mirrored) {
      Matrix4f poseMatrix = pose.pose();
      Matrix3f normalMatrix = pose.normal();
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
      float halfWidth = width * 0.5F;
      float height = entity.getBbHeight() * 0.5F;
      consumer.addVertex(poseMatrix, -halfWidth, height, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, mirrored ? 0.0F : 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, height, -halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, mirrored ? 0.0F : 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, halfWidth, height, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(1.0F, mirrored ? 1.0F : 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      consumer.addVertex(poseMatrix, -halfWidth, height, halfWidth)
         .setColor(255, 255, 255, 255)
         .setUv(0.0F, mirrored ? 1.0F : 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(EsotericStrike esotericStrike) {
      int frame = esotericStrike.tickCount / 2 % TEXTURES.length;
      return TEXTURES[frame];
   }
}
