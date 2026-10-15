package com.somake.somakespells.render;

import com.github.L_Ender.cataclysm.client.render.entity.Wave_Renderer;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.BigWaveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class BigWaveRenderer extends Wave_Renderer {
   public BigWaveRenderer(Context context) {
      super(context);
   }

   public void render(Wave_Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      if (entity instanceof BigWaveEntity) {
         poseStack.pushPose();
         float scale = 3.0F;
         poseStack.scale(scale, scale, scale);
         super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
         poseStack.popPose();
      } else {
         super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }
}
