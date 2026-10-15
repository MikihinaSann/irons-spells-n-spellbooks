package net.hazen.hazennstuff.Item.Weapons.Generic.TheDevourer;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class TheDevourerModel extends DefaultedItemGeoModel<TheDevourerItem> {
   public TheDevourerModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(TheDevourerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/the_devourer.geo.json");
   }

   public ResourceLocation getTextureResource(TheDevourerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/the_devourer.png");
   }

   public ResourceLocation getAnimationResource(TheDevourerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
