package net.hazen.hazennstuff.Item.Weapons.Generic.Skyscorcher;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class SkyscorcherModel extends DefaultedItemGeoModel<SkyscorcherItem> {
   public SkyscorcherModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SkyscorcherItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/skyscorcher.geo.json");
   }

   public ResourceLocation getTextureResource(SkyscorcherItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/skyscorcher.png");
   }

   public ResourceLocation getAnimationResource(SkyscorcherItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
