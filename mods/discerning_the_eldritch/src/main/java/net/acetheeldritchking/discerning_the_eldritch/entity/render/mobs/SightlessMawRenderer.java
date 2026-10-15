package net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw.SightlessMawEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SightlessMawRenderer extends GeoEntityRenderer<SightlessMawEntity> {
   public SightlessMawRenderer(Context renderManager, GeoModel<SightlessMawEntity> model) {
      super(renderManager, model);
      this.shadowRadius = 0.5F;
   }

   public ResourceLocation getTextureLocation(SightlessMawEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/sightless_maw/sightless_maw.png");
   }

   public void render(SightlessMawEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }
}
