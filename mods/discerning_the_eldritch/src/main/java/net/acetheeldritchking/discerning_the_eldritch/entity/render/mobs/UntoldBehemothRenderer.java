package net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.untold_behemoth.UntoldBehemothEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class UntoldBehemothRenderer extends GeoEntityRenderer<UntoldBehemothEntity> {
   public UntoldBehemothRenderer(Context renderManager, GeoModel<UntoldBehemothEntity> model) {
      super(renderManager, model);
      this.shadowRadius = 1.0F;
   }

   public ResourceLocation getTextureLocation(UntoldBehemothEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/untold_behemoth/untold_behemoth.png");
   }

   public void render(UntoldBehemothEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }
}
