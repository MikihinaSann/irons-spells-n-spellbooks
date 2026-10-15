package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.client.model.IncineratorSwordModel;
import com.somake.somakespells.entity.IncineratorSlashEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class IncineratorSlashRenderer extends EntityRenderer<IncineratorSlashEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/item/the_incinerator.png");
   private final IncineratorSwordModel<IncineratorSlashEntity> model;

   public IncineratorSlashRenderer(Context context) {
      super(context);
      this.model = new IncineratorSwordModel(context.bakeLayer(IncineratorSwordModel.LAYER_LOCATION));
   }

   public void render(IncineratorSlashEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
      poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
      float age = entity.tickCount + partialTicks;
      float maxAge = 20.0F;
      float progress = Math.min(age / maxAge, 1.0F);
      float startAngle = 90.0F;
      float endAngle = 225.0F;
      float currentAngle = Mth.lerp(progress, startAngle, endAngle);
      poseStack.mulPose(Axis.YP.rotationDegrees(currentAngle));
      poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
      poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
      poseStack.scale(3.0F, 3.0F, 3.0F);
      poseStack.translate(0.0, 0.0, -0.5);
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
      this.model.renderToBuffer(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(IncineratorSlashEntity entity) {
      return TEXTURE;
   }
}
