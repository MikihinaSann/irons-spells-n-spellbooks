package net.acetheeldritchking.discerning_the_eldritch.entity.spells.blade_of_rancor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class BladeOfRancorRenderer extends GeoEntityRenderer<BladeOfRancorProjectile> {
   public BladeOfRancorRenderer(Context renderManager) {
      super(renderManager, new BladeOfRancorModel());
   }

   public ResourceLocation getTextureLocation(BladeOfRancorProjectile animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/rancor_blade/rancor_blade.png");
   }

   public void preRender(
      PoseStack poseStack,
      BladeOfRancorProjectile animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
      poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }

   public Color getRenderColor(BladeOfRancorProjectile animatable, float partialTick, int packedLight) {
      return Color.LIGHT_GRAY;
   }
}
