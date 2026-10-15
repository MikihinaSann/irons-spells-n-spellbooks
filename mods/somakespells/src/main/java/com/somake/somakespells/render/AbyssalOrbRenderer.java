package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.client.model.IgnisFireballModel;
import com.somake.somakespells.entity.AbyssalOrbEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class AbyssalOrbRenderer extends EntityRenderer<AbyssalOrbEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/ignis_fireball_abyss.png");
   private final IgnisFireballModel<AbyssalOrbEntity> model;

   public AbyssalOrbRenderer(Context context) {
      super(context);
      this.model = new IgnisFireballModel(context.bakeLayer(IgnisFireballModel.LAYER_LOCATION));
   }

   public void render(AbyssalOrbEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(Math.abs(entity.getXRot()) - 180.0F));
      poseStack.mulPose(Axis.XP.rotationDegrees(Math.abs(entity.getXRot())));
      float f = entity.tickCount + partialTicks;
      this.model.setupAnim(entity, 0.0F, 0.0F, f, entityYaw, entity.getXRot());
      VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
      this.model.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
      poseStack.popPose();
      super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(AbyssalOrbEntity entity) {
      return TEXTURE;
   }
}
