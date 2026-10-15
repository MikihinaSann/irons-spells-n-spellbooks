package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.SovereignArmoryPortalEntity;
import com.somake.somakespells.entity.model.SovereignArmoryPortalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SovereignArmoryPortalRenderer extends GeoEntityRenderer<SovereignArmoryPortalEntity> {
   public SovereignArmoryPortalRenderer(Context context) {
      super(context, new SovereignArmoryPortalModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(SovereignArmoryPortalEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
   }

   public void render(
      SovereignArmoryPortalEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
   ) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));
      poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot())));
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);
      poseStack.popPose();
   }
}
