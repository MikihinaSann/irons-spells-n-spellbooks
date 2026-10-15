package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.FragmentedSwordRainEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

public class FragmentedSwordRainRenderer extends EntityRenderer<FragmentedSwordRainEntity> {
   private final ItemRenderer itemRenderer;

   public FragmentedSwordRainRenderer(Context context) {
      super(context);
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(FragmentedSwordRainEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      if (entity.shouldRenderSword()) {
         poseStack.pushPose();
         poseStack.translate(0.0, entity.getSwordYOffset(), 0.0);
         poseStack.mulPose(new Quaternionf().rotationXYZ((float)Math.toRadians(entity.getSwordPitch()), 0.0F, 0.0F));
         this.itemRenderer
            .renderStatic(
               entity.getRenderStack(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId()
            );
         poseStack.popPose();
         super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
      }
   }

   public ResourceLocation getTextureLocation(FragmentedSwordRainEntity entity) {
      return null;
   }
}
