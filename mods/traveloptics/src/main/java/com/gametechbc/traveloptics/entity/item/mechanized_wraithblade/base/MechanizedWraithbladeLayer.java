package com.gametechbc.traveloptics.entity.item.mechanized_wraithblade.base;

import com.gametechbc.traveloptics.TravelopticsMod;
import com.gametechbc.traveloptics.item.bossweapon.mechanized_wraithblade.MechanizedWraithbladeItem;
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

public class MechanizedWraithbladeLayer extends GeoRenderLayer<MechanizedWraithbladeItem> {
   public static final ResourceLocation TEXTURE = TravelopticsMod.id("textures/models/item/mechanized_wraithblade_layer.png");

   public MechanizedWraithbladeLayer(GeoRenderer<MechanizedWraithbladeItem> itemRenderer) {
      super(itemRenderer);
   }

   public void render(
      PoseStack poseStack,
      MechanizedWraithbladeItem animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      renderType = RenderType.eyes(TEXTURE);
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
