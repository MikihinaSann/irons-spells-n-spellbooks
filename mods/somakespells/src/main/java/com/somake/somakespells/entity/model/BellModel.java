package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.BellEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BellModel extends GeoModel<BellEntity> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/entity/bell.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/bell.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("somakespells", "animations/entity/bell.animation.json");

   public ResourceLocation getModelResource(BellEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(BellEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(BellEntity animatable) {
      return ANIMATION;
   }
}
