package com.somake.somakespells.client.model;

import com.somake.somakespells.item.armor.CeraniumArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CeraniumArmorModel extends GeoModel<CeraniumArmorItem> {
   public ResourceLocation getModelResource(CeraniumArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "geo/armor/ceranium_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CeraniumArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/ceranium_armor.png");
   }

   public ResourceLocation getAnimationResource(CeraniumArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "animations/armor/ceranium_armor.animation.json");
   }
}
