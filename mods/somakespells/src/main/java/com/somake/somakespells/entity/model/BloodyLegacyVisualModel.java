package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.BloodyLegacyVisualEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodyLegacyVisualModel extends GeoModel<BloodyLegacyVisualEntity> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/entity/bloody_legacy.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/bloody_legacy.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("somakespells", "animations/entity/bloody_legacy.animation.json");

   public ResourceLocation getModelResource(BloodyLegacyVisualEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(BloodyLegacyVisualEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(BloodyLegacyVisualEntity animatable) {
      return ANIMATION;
   }
}
