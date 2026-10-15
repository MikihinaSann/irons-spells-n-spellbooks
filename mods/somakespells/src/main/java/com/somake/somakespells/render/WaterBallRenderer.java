package com.somake.somakespells.render;

import com.somake.somakespells.entity.WaterBallEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WaterBallRenderer extends GeoEntityRenderer<WaterBallEntity> {
   public WaterBallRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel<WaterBallEntity>(ResourceLocation.fromNamespaceAndPath("somakespells", "waterball"), true) {
         public ResourceLocation getTextureResource(WaterBallEntity animatable) {
            return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/projectile/waterball/waterball.png");
         }
      });
   }
}
