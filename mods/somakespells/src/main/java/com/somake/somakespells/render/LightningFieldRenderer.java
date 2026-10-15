package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.LightningFieldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class LightningFieldRenderer extends EntityRenderer<LightningFieldEntity> {
   public LightningFieldRenderer(Context context) {
      super(context);
   }

   public void render(LightningFieldEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
   }

   public ResourceLocation getTextureLocation(LightningFieldEntity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
