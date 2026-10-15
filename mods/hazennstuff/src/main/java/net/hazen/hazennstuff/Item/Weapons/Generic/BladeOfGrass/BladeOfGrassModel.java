package net.hazen.hazennstuff.Item.Weapons.Generic.BladeOfGrass;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BladeOfGrassModel extends DefaultedItemGeoModel<BladeOfGrassItem> {
   public BladeOfGrassModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(BladeOfGrassItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/blade_of_grass.geo.json");
   }

   public ResourceLocation getTextureResource(BladeOfGrassItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/blade_of_grass.png");
   }

   public ResourceLocation getAnimationResource(BladeOfGrassItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
