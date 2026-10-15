package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.BloodCrystalRootVisualEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodCrystalRootVisualModel extends GeoModel<BloodCrystalRootVisualEntity> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/entity/blood_crystal_root.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/crimson_root_crystal.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "animations/entity/blood_crystal_root.animation.json"
   );

   public ResourceLocation getModelResource(BloodCrystalRootVisualEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(BloodCrystalRootVisualEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(BloodCrystalRootVisualEntity animatable) {
      return ANIMATION;
   }
}
