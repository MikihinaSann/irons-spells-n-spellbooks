package net.warphan.iss_magicfromtheeast.entity.spells.anchoring_kunai;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.RenderHelper.CustomerRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AnchoringKunaiRenderer extends EntityRenderer<AnchoringKunaiProjectile> {
   private static final ResourceLocation TEXTURE = ISS_MagicFromTheEast.id("textures/entity/anchoring_kunai.png");

   public AnchoringKunaiRenderer(Context context) {
      super(context);
   }

   public void render(AnchoringKunaiProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Vec3 motion = entity.deltaMovementOld.add(entity.getDeltaMovement().subtract(entity.deltaMovementOld).scale(partialTicks));
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      renderModel(poseStack, bufferSource);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
      poseStack.scale(0.075F, 0.05625F, 0.05625F);
      Pose pose = poseStack.last();
      Matrix4f poseMatrix = pose.pose();
      Matrix3f normalMatrix = pose.normal();
      VertexConsumer consumer = bufferSource.getBuffer(CustomerRenderType.magic(getTextureLocation()));
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-2.0F, 0.0F, 0.0F);

      for (int j = 0; j < 4; j++) {
         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
         vertex(poseMatrix, normalMatrix, consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, 15728880);
         vertex(poseMatrix, normalMatrix, consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, 15728880);
         vertex(poseMatrix, normalMatrix, consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, 15728880);
         vertex(poseMatrix, normalMatrix, consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, 15728880);
      }
   }

   public static void vertex(
      Matrix4f pMatrix,
      Matrix3f pNormals,
      VertexConsumer pVertexBuilder,
      int pOffsetX,
      int pOffsetY,
      int pOffsetZ,
      float pTextureX,
      float pTextureY,
      int pNormalX,
      int p_113835_,
      int p_113836_,
      int pPackedLight
   ) {
      pVertexBuilder.addVertex(pMatrix, pOffsetX, pOffsetY, pOffsetZ)
         .setColor(255, 255, 255, 255)
         .setUv(pTextureX, pTextureY)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(pPackedLight)
         .setNormal(pNormalX, p_113836_, p_113835_);
   }

   public ResourceLocation getTextureLocation(AnchoringKunaiProjectile entity) {
      return getTextureLocation();
   }

   public static ResourceLocation getTextureLocation() {
      return TEXTURE;
   }
}
