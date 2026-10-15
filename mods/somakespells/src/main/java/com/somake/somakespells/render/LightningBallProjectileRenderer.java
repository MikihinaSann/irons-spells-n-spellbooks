package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.entity.LightningBallProjectile;
import io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightningRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class LightningBallProjectileRenderer extends EntityRenderer<LightningBallProjectile> {
   private final BallLightningRenderer delegate;

   public LightningBallProjectileRenderer(Context context) {
      super(context);
      this.delegate = new BallLightningRenderer(context);
   }

   public void render(LightningBallProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      this.delegate.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(LightningBallProjectile entity) {
      return this.delegate.getTextureLocation(entity);
   }
}
