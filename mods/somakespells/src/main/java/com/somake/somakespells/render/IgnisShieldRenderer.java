package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import com.somake.somakespells.client.model.BulwarkOfTheFlameModel;
import com.somake.somakespells.entity.IgnisShieldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class IgnisShieldRenderer extends EntityRenderer<IgnisShieldEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/ignis/ignis_shield.png");
   private final BulwarkOfTheFlameModel<IgnisShieldEntity> model;

   public IgnisShieldRenderer(Context context) {
      super(context);
      this.model = new BulwarkOfTheFlameModel(context.bakeLayer(BulwarkOfTheFlameModel.LAYER_LOCATION));
   }

   public void render(IgnisShieldEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Pose pose = poseStack.last();
      Matrix4f poseMatrix = pose.pose();
      Matrix3f normalMatrix = pose.normal();
      poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
      poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
      float width = entity.getBbWidth() * 0.65F;
      poseStack.scale(width, width, width);
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
      this.model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, -1);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(IgnisShieldEntity entity) {
      return TEXTURE;
   }
}
