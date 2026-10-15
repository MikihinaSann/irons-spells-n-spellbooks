package net.alshanex.tunes_n_tomes.entity.aria_beam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AriaBeamRenderer extends GeoEntityRenderer<AriaBeamEntity> {
   public AriaBeamRenderer(Context renderManager) {
      super(renderManager, new AriaBeamModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public void render(AriaBeamEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      LivingEntity owner = entity.getOwner();
      if (owner != null) {
         double ownerX = Mth.lerp(partialTick, owner.xOld, owner.getX());
         double ownerY = Mth.lerp(partialTick, owner.yOld, owner.getY());
         double ownerZ = Mth.lerp(partialTick, owner.zOld, owner.getZ());
         double entityX = Mth.lerp(partialTick, entity.xOld, entity.getX());
         double entityY = Mth.lerp(partialTick, entity.yOld, entity.getY());
         double entityZ = Mth.lerp(partialTick, entity.zOld, entity.getZ());
         float yaw = Mth.lerp(partialTick, owner.yRotO, owner.getYRot());
         float pitch = Mth.lerp(partialTick, owner.xRotO, owner.getXRot());
         double eyeHeightOffset = owner.getEyeHeight() - 0.8;
         poseStack.pushPose();
         poseStack.translate(ownerX - entityX, ownerY - entityY + eyeHeightOffset, ownerZ - entityZ);
         poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
         poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
         super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
         poseStack.popPose();
      } else {
         super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      }
   }
}
