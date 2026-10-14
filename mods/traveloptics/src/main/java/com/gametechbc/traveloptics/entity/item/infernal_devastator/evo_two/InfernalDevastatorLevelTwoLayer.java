package com.gametechbc.traveloptics.entity.item.infernal_devastator.evo_two;

import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelTwoItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class InfernalDevastatorLevelTwoLayer extends GeoRenderLayer<InfernalDevastatorLevelTwoItem> {
   private static final ResourceLocation LAYER = new ResourceLocation("traveloptics", "textures/models/item/infernal_devastator_layer.png");

   public InfernalDevastatorLevelTwoLayer(GeoRenderer<InfernalDevastatorLevelTwoItem> itemRenderer) {
      super(itemRenderer);
   }

   public void render(
      PoseStack poseStack,
      InfernalDevastatorLevelTwoItem animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      renderType = RenderType.eyes(LAYER);
      VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
      poseStack.pushPose();
      this.getRenderer()
         .actuallyRender(
            poseStack,
            animatable,
            bakedModel,
            renderType,
            bufferSource,
            vertexconsumer,
            true,
            partialTick,
            15728880,
            OverlayTexture.NO_OVERLAY,
            Utils.packRGB(new Vector3f(1.0F, 1.0F, 1.0F))
         );
      poseStack.popPose();
   }
}
