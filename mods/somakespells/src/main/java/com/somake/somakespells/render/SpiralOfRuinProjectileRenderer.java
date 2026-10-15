package com.somake.somakespells.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.client.model.SpiralOfRuinTridentModel;
import com.somake.somakespells.entity.SpiralOfRuinProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;

public class SpiralOfRuinProjectileRenderer extends EntityRenderer<SpiralOfRuinProjectileEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "legendary_monsters", "textures/entity/posessed_paladin/layer/possessed_paladin_trident_layer.png"
   );
   private static final ResourceLocation RED_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "legendary_monsters", "textures/entity/posessed_paladin/layer/possessed_paladin_trident_red_layer.png"
   );
   private final SpiralOfRuinTridentModel model;

   public SpiralOfRuinProjectileRenderer(Context context) {
      super(context);
      this.model = new SpiralOfRuinTridentModel(context.bakeLayer(SpiralOfRuinTridentModel.LAYER_LOCATION));
   }

   public void render(SpiralOfRuinProjectileEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));
      poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, -entity.xRotO, -entity.getXRot())));
      poseStack.scale(1.5F, 1.5F, 1.5F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer, this.model.renderType(this.getTextureLocation(entity)), false, false);
      this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, ARGB32.colorFromFloat(0.5F, 1.0F, 1.0F, 1.0F));
      RenderSystem.disableBlend();
      poseStack.popPose();
      super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
   }

   public ResourceLocation getTextureLocation(SpiralOfRuinProjectileEntity entity) {
      return entity.isRedSoul() ? RED_TEXTURE : TEXTURE;
   }
}
