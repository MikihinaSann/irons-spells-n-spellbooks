package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.client.model.SoulBastionShieldModel;
import com.somake.somakespells.entity.SoulBastionShieldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;

public class SoulBastionShieldRenderer extends EntityRenderer<SoulBastionShieldEntity> {
   private static final ResourceLocation BASE_LAYER = ResourceLocation.fromNamespaceAndPath(
      "legendary_monsters", "textures/entity/posessed_paladin/soul_shield.png"
   );
   private static final ResourceLocation RED_LAYER = ResourceLocation.fromNamespaceAndPath(
      "legendary_monsters", "textures/entity/posessed_paladin/soul_shield_red.png"
   );
   private final SoulBastionShieldModel<SoulBastionShieldEntity> model;

   public SoulBastionShieldRenderer(Context context) {
      super(context);
      this.model = new SoulBastionShieldModel<>(context.bakeLayer(SoulBastionShieldModel.LAYER_LOCATION));
   }

   public void render(SoulBastionShieldEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.pushPose();
      poseStack.scale(1.75F, 1.75F, 1.75F);
      poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot() + 180.0F));
      poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getXRot()));
      poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
      poseStack.translate(0.0, -1.5, 0.0);
      float animationProgress = Math.min(entity.getAnimationProgress(partialTick), 1.0F);
      float alphaProgress = 1.0F - animationProgress;
      VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
      this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);
      this.model
         .renderToBuffer(
            poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, ARGB32.colorFromFloat(Mth.clamp(alphaProgress - 0.5F, 0.0F, 1.0F), 1.0F, 1.0F, 1.0F)
         );
      poseStack.popPose();
      super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
   }

   public ResourceLocation getTextureLocation(SoulBastionShieldEntity entity) {
      return entity.isRed() ? RED_LAYER : BASE_LAYER;
   }
}
