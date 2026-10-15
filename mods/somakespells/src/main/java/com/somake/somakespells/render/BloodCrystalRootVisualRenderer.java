package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.BloodCrystalRootVisualEntity;
import com.somake.somakespells.entity.model.BloodCrystalRootVisualModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodCrystalRootVisualRenderer extends GeoEntityRenderer<BloodCrystalRootVisualEntity> {
   public BloodCrystalRootVisualRenderer(Context context) {
      super(context, new BloodCrystalRootVisualModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(BloodCrystalRootVisualEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucentEmissive(texture);
   }

   public void render(BloodCrystalRootVisualEntity visual, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      LivingEntity target = visual.getTarget();
      if (target != null && target.isAlive()) {
         float bodyYaw = Mth.rotLerp(partialTick, target.yBodyRotO, target.yBodyRot);
         float widthScale = Mth.clamp(target.getBbWidth() / 0.6F, 0.75F, 2.2F);
         float heightScale = Mth.clamp(target.getBbHeight() / 1.8F, 0.65F, 1.8F);
         float pulse = 1.0F + Mth.sin((visual.tickCount + partialTick) * 0.38F) * 0.035F;
         poseStack.pushPose();
         poseStack.translate(0.0, target.getBbHeight() * 0.58, 0.0);
         poseStack.mulPose(Axis.YP.rotationDegrees(-bodyYaw));
         poseStack.scale(widthScale * pulse, heightScale * pulse, widthScale * pulse);
         super.render(visual, entityYaw, partialTick, poseStack, buffer, 15728880);
         poseStack.popPose();
      }
   }
}
