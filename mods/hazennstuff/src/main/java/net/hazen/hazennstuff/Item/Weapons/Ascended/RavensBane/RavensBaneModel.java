package net.hazen.hazennstuff.Item.Weapons.Ascended.RavensBane;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RavensBaneModel extends DefaultedItemGeoModel<RavensBaneItem> {
   public RavensBaneModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(RavensBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ravens_bane.geo.json");
   }

   public ResourceLocation getTextureResource(RavensBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ravens_bane.png");
   }

   public ResourceLocation getAnimationResource(RavensBaneItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
