package net.hazen.hazennstuff.Item.Weapons.Generic.Firebrand;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class FirebrandModel extends DefaultedItemGeoModel<FirebrandItem> {
   public FirebrandModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FirebrandItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/firebrand.geo.json");
   }

   public ResourceLocation getTextureResource(FirebrandItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/firebrand.png");
   }

   public ResourceLocation getAnimationResource(FirebrandItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
