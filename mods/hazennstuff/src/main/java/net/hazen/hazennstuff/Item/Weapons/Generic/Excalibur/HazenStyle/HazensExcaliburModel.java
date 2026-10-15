package net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.HazenStyle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class HazensExcaliburModel extends DefaultedItemGeoModel<HazensExcaliburItem> {
   public HazensExcaliburModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(HazensExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/excalibur_hazen_style.geo.json");
   }

   public ResourceLocation getTextureResource(HazensExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/excalibur_hazen_style.png");
   }

   public ResourceLocation getAnimationResource(HazensExcaliburItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
