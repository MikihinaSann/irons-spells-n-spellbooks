package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.SomakeGhostEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SomakeGhostModel extends GeoModel<SomakeGhostEntity> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/entity/ghost.geo.json");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("somakespells", "animations/entity/ghost.animation.json");

   public ResourceLocation getModelResource(SomakeGhostEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(SomakeGhostEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", animatable.getElement().texturePath());
   }

   public ResourceLocation getAnimationResource(SomakeGhostEntity animatable) {
      return ANIMATION;
   }
}
