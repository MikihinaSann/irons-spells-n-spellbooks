package com.somake.somakespells.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.LivingEntity;

public class GuardianConnectionSwirlLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation SUNBLOCK_ARMOR = ResourceLocation.fromNamespaceAndPath("mowziesmobs", "textures/entity/sunblock_glow.png");

   public GuardianConnectionSwirlLayer(RenderLayerParent<T, M> entityRendererIn) {
      super(entityRendererIn);
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      T entity,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (ModMobEffects.GUARDIAN_CONNECTION != null && entity.hasEffect(ModMobEffects.GUARDIAN_CONNECTION)) {
         float tickTime = entity.tickCount + partialTick;
         EntityModel<T> entityModel = this.getParentModel();
         entityModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
         this.getParentModel().copyPropertiesTo(entityModel);
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(tickTime), tickTime * 0.01F));
         entityModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         entityModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, ARGB32.colorFromFloat(1.0F, 1.0F, 1.0F, 0.1F));
      }
   }

   protected float xOffset(float tickTime) {
      return tickTime * 0.02F;
   }

   protected ResourceLocation getTextureLocation() {
      return SUNBLOCK_ARMOR;
   }
}
