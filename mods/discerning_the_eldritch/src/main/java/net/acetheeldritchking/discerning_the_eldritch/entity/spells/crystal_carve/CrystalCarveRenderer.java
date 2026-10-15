package net.acetheeldritchking.discerning_the_eldritch.entity.spells.crystal_carve;

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

public class CrystalCarveRenderer extends EntityRenderer<CrystalCarveEntity> {
   private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
      DiscerningTheEldritch.id("textures/entity/crystal_carve/crystal_carve_1.png"),
      DiscerningTheEldritch.id("textures/entity/crystal_carve/crystal_carve_2.png"),
      DiscerningTheEldritch.id("textures/entity/crystal_carve/crystal_carve_3.png"),
      DiscerningTheEldritch.id("textures/entity/crystal_carve/crystal_carve_4.png")
   };
   private static final ResourceLocation[] TEXTURES_FINAL = new ResourceLocation[]{
      DiscerningTheEldritch.id("textures/entity/crystal_carve_final/crystal_carve_final_1.png"),
      DiscerningTheEldritch.id("textures/entity/crystal_carve_final/crystal_carve_final_2.png"),
      DiscerningTheEldritch.id("textures/entity/crystal_carve_final/crystal_carve_final_3.png"),
      DiscerningTheEldritch.id("textures/entity/crystal_carve_final/crystal_carve_final_4.png")
   };

   public CrystalCarveRenderer(Context context) {
      super(context);
   }

   public void render(CrystalCarveEntity carver, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - carver.getYRot()));
      poseStack.mulPose(Axis.ZP.rotationDegrees(carver.getXRot()));
      float randomZ = new Random(31L * carver.getId()).nextInt(-5, 5);
      poseStack.mulPose(Axis.XP.rotationDegrees(randomZ));
      this.drawSlash(pose, carver, bufferSource, carver.getBbWidth() * 1.5F, carver.isMirrored());
      poseStack.popPose();
      super.render(carver, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   private void drawSlash(Pose pose, CrystalCarveEntity entity, MultiBufferSource bufferSource, float width, boolean mirrored) {
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

   public ResourceLocation getTextureLocation(CrystalCarveEntity crystalCarveEntity) {
      if (crystalCarveEntity.getIsFinal()) {
         int frame = crystalCarveEntity.tickCount / 2 % TEXTURES_FINAL.length;
         return TEXTURES_FINAL[frame];
      } else {
         int frame = crystalCarveEntity.tickCount / 2 % TEXTURES.length;
         return TEXTURES[frame];
      }
   }
}
