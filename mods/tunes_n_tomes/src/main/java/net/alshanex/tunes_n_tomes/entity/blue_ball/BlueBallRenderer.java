package net.alshanex.tunes_n_tomes.entity.blue_ball;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlueBallRenderer extends GeoEntityRenderer<BlueBallEntity> {
   public BlueBallRenderer(Context context) {
      super(context, new BlueBallModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public void render(BlueBallEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      Vec3 motion = entity.getDeltaMovement();
      double horizontalDistance = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
      float yaw = (float)(Mth.atan2(motion.x, motion.z) * (180.0 / Math.PI));
      float pitch = (float)(Mth.atan2(motion.y, horizontalDistance) * (180.0 / Math.PI));
      poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
      poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      poseStack.popPose();
   }
}
