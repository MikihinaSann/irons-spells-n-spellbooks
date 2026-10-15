package net.hazen.hazennstuff.Item.Weapons.Generic.Hammush;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class HammushModel extends DefaultedItemGeoModel<HammushItem> {
   public HammushModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(HammushItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/hammush.geo.json");
   }

   public ResourceLocation getTextureResource(HammushItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/hammush.png");
   }

   public ResourceLocation getAnimationResource(HammushItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
