package net.hazen.hazennstuff.Item.Weapons.Generic.Starfury;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class StarfuryModel extends DefaultedItemGeoModel<StarfuryItem> {
   public StarfuryModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(StarfuryItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/starfury.geo.json");
   }

   public ResourceLocation getTextureResource(StarfuryItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/starfury.png");
   }

   public ResourceLocation getAnimationResource(StarfuryItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
