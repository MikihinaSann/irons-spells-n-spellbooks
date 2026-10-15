package net.alshanex.tunes_n_tomes.entity.hymn_of_hope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HopeShieldRenderer extends GeoEntityRenderer<HopeShieldEntity> {
   public HopeShieldRenderer(Context renderManager) {
      super(renderManager, new HopeShieldModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public void render(HopeShieldEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      LivingEntity owner = entity.getOwner();
      if (owner != null) {
         double ownerX = Mth.lerp(partialTick, owner.xOld, owner.getX());
         double ownerY = Mth.lerp(partialTick, owner.yOld, owner.getY());
         double ownerZ = Mth.lerp(partialTick, owner.zOld, owner.getZ());
         double entityX = Mth.lerp(partialTick, entity.xOld, entity.getX());
         double entityY = Mth.lerp(partialTick, entity.yOld, entity.getY());
         double entityZ = Mth.lerp(partialTick, entity.zOld, entity.getZ());
         poseStack.pushPose();
         poseStack.translate(ownerX - entityX, ownerY - entityY, ownerZ - entityZ);
         super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
         poseStack.popPose();
      } else {
         super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      }
   }
}
