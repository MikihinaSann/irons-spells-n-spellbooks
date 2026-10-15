package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.ThrustLightningEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThrustLightningModel extends GeoModel<ThrustLightningEntity> {
   public ResourceLocation getModelResource(ThrustLightningEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "geo/thrust_lightning.geo.json");
   }

   public ResourceLocation getTextureResource(ThrustLightningEntity animatable) {
      int frame = Math.min(animatable.tickCount + 1, 9);
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/particle/thrust_lightning_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(ThrustLightningEntity animatable) {
      return null;
   }
}
