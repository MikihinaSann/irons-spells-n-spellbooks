package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.MirrorStrikeVisualEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MirrorStrikeVisualModel extends GeoModel<MirrorStrikeVisualEntity> {
   public ResourceLocation getModelResource(MirrorStrikeVisualEntity object) {
      return ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "geo/bagua_circle.geo.json");
   }

   public ResourceLocation getTextureResource(MirrorStrikeVisualEntity object) {
      return ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "textures/entity/bagua_circle.png");
   }

   public ResourceLocation getAnimationResource(MirrorStrikeVisualEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "animations/entity/mirror_strike.animation.json");
   }
}
