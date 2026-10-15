package com.somake.somakespells.client.model;

import com.somake.somakespells.item.armor.AquamancerArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AquamancerArmorModel extends GeoModel<AquamancerArmorItem> {
   public ResourceLocation getModelResource(AquamancerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "geo/armor/aquamancer_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AquamancerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/aquamancer_armor.png");
   }

   public ResourceLocation getAnimationResource(AquamancerArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "animations/armor/aquamancer_armor.animation.json");
   }
}
