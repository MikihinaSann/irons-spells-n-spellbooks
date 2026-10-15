package net.alshanex.alshanex_familiars.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class PurpurPilarEntityRenderer extends EntityRenderer<PurpurPilarEntity> {
   private final BlockRenderDispatcher blockRenderer;

   public PurpurPilarEntityRenderer(Context context) {
      super(context);
      this.blockRenderer = context.getBlockRenderDispatcher();
   }

   public void render(PurpurPilarEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      poseStack.pushPose();
      this.blockRenderer.renderSingleBlock(Blocks.PURPUR_PILLAR.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
      Vec3 motion = entity.getDeltaMovement();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      poseStack.popPose();
      super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
   }

   public ResourceLocation getTextureLocation(PurpurPilarEntity entity) {
      return null;
   }
}
