package net.hazen.hazennstuff.Item.Weapons.Generic.LightsBane;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class LightsBaneModel extends DefaultedItemGeoModel<LightsBaneItem> {
   public LightsBaneModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(LightsBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/lights_bane.geo.json");
   }

   public ResourceLocation getTextureResource(LightsBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/lights_bane.png");
   }

   public ResourceLocation getAnimationResource(LightsBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
