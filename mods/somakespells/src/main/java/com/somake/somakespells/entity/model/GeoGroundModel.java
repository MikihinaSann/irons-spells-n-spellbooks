package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.GeoGroundEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeoGroundModel extends DefaultedEntityGeoModel<GeoGroundEntity> {
   public GeoGroundModel() {
      super(ResourceLocation.fromNamespaceAndPath("somakespells", "geo_ground"));
   }

   public ResourceLocation getTextureResource(GeoGroundEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/vfx_ground_pound.png");
   }
}
