package com.somake.somakespells.client.model;

import com.somake.somakespells.item.armor.DarkMetalBattlemageArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DarkMetalBattlemageArmorModel extends GeoModel<DarkMetalBattlemageArmorItem> {
   public ResourceLocation getModelResource(DarkMetalBattlemageArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "geo/armor/dark_metal_battlemage.geo.json");
   }

   public ResourceLocation getTextureResource(DarkMetalBattlemageArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/dark_metal_battlemage.png");
   }

   public ResourceLocation getAnimationResource(DarkMetalBattlemageArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "animations/armor/dark_metal_battlemage.animation.json");
   }
}
