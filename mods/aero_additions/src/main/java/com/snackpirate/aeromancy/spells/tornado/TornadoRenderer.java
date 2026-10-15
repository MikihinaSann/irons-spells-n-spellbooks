package com.snackpirate.aeromancy.spells.tornado;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.snackpirate.aeromancy.Aeromancy;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class TornadoRenderer extends DynamicGeoEntityRenderer<TornadoEntity> {
   public TornadoRenderer(Context renderManager) {
      super(renderManager, new TornadoModel());
   }

   @Nullable
   protected ResourceLocation getTextureOverrideForBone(GeoBone bone, TornadoEntity animatable, float partialTick) {
      return !bone.getName().contains("top") ? Aeromancy.id("textures/entity/tornado.png") : Aeromancy.id("textures/entity/tornado_top.png");
   }

   @Nullable
   public RenderType getRenderType(TornadoEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      float f = animatable.tickCount + partialTick;
      return RenderType.breezeWind(texture, this.xOffset(f) % 1.0F, 0.0F);
   }

   @Nullable
   protected RenderType getRenderTypeOverrideForBone(
      GeoBone bone, TornadoEntity animatable, ResourceLocation texturePath, MultiBufferSource bufferSource, float partialTick
   ) {
      float f = animatable.tickCount + partialTick;
      return RenderType.breezeWind(texturePath, !bone.getName().contains("top") ? this.xOffset(f) % 1.0F : 0.0F, 0.0F);
   }

   public Color getRenderColor(TornadoEntity animatable, float partialTick, int packedLight) {
      int colorValue = switch (animatable.getEffect()) {
         case DEFAULT -> 16777215;
         case FIRE -> 16753920;
         case ENDER -> 16711935;
         case NATURE -> 65280;
         case LIGHTNING -> 65535;
      };
      return new Color(-805306368 + colorValue);
   }

   private float xOffset(float tickCount) {
      return tickCount * 0.02F;
   }

   public void preRender(
      PoseStack poseStack,
      TornadoEntity animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.scale(animatable.getSize() / 6.5F, animatable.getSize() / 6.5F, animatable.getSize() / 6.5F);
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }
}
