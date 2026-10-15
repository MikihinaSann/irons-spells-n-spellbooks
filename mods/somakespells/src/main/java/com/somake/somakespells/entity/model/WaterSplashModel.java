package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.WaterSplashEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WaterSplashModel extends DefaultedEntityGeoModel<WaterSplashEntity> {
   public WaterSplashModel() {
      super(ResourceLocation.fromNamespaceAndPath("somakespells", "water_splash"));
   }

   public ResourceLocation getTextureResource(WaterSplashEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/water_splash.png");
   }
}
