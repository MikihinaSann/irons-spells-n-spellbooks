package net.hazen.hazennstuff.Item.Weapons.Generic.FrostburnDagger;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class FrostburnDaggerModel extends DefaultedItemGeoModel<FrostburnDaggerItem> {
   public FrostburnDaggerModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FrostburnDaggerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/frostburn_dagger.geo.json");
   }

   public ResourceLocation getTextureResource(FrostburnDaggerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/frostburn_dagger.png");
   }

   public ResourceLocation getAnimationResource(FrostburnDaggerItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
