package com.somake.somakespells.client.model;

import com.somake.somakespells.item.armor.AbyssiumArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbyssiumArmorModel extends GeoModel<AbyssiumArmorItem> {
   public ResourceLocation getModelResource(AbyssiumArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "geo/armor/abyssium_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AbyssiumArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/abyssium_armor.png");
   }

   public ResourceLocation getAnimationResource(AbyssiumArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "animations/armor/abyssium_armor.animation.json");
   }
}
