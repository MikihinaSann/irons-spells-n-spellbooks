package com.snackpirate.aeromancy.spells.wind_blade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.snackpirate.aeromancy.item.AAItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class WindBladeRenderer extends EntityRenderer<WindBladeProjectile> {
   private final ItemRenderer itemRenderer;

   public WindBladeRenderer(Context context) {
      super(context);
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      WindBladeProjectile entity, float entityYaw, float partialTick, PoseStack matrixStackIn, MultiBufferSource bufferSource, int packedLightIn
   ) {
      if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.0, entity.getBoundingBox().getYsize() * 0.5, 0.0);
         Vec3 motion = entity.getDeltaMovement();
         float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
         float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(yRot));
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(xRot + 90.0F));
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-135.0F));
         matrixStackIn.translate(-0.03125, -0.09375, 0.0);
         this.itemRenderer
            .renderStatic(
               new ItemStack(AAItems.WIND_SWORD),
               ItemDisplayContext.GROUND,
               packedLightIn,
               OverlayTexture.NO_OVERLAY,
               matrixStackIn,
               bufferSource,
               entity.level(),
               entity.getId()
            );
         matrixStackIn.popPose();
         super.render(entity, entityYaw, partialTick, matrixStackIn, bufferSource, packedLightIn);
      }
   }

   public ResourceLocation getTextureLocation(WindBladeProjectile windBladeProjectile) {
      return null;
   }
}
