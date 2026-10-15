package net.warphan.iss_magicfromtheeast.entity.armor;

import net.minecraft.resources.ResourceLocation;
import net.warphan.iss_magicfromtheeast.item.armor.JadePagodaArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class JadePagodaArmorModel extends DefaultedItemGeoModel<JadePagodaArmorItem> {
   public JadePagodaArmorModel() {
      super(new ResourceLocation("iss_magicfromtheeast", ""));
   }

   public ResourceLocation getModelResource(JadePagodaArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "geo/jade_pagoda.geo.json");
   }

   public ResourceLocation getTextureResource(JadePagodaArmorItem object) {
      return new ResourceLocation("iss_magicfromtheeast", "textures/models/armor/jade_pagoda.png");
   }

   public ResourceLocation getAnimationResource(JadePagodaArmorItem animatable) {
      return new ResourceLocation("iss_magicfromtheeast", "animations/master_armor_animation.json");
   }
}
