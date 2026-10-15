package net.hazen.hazennstuff.Item.Weapons.Generic.Meowmere;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MeowmereModel extends DefaultedItemGeoModel<MeowmereItem> {
   public MeowmereModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MeowmereItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/meowmere.geo.json");
   }

   public ResourceLocation getTextureResource(MeowmereItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/meowmere.png");
   }

   public ResourceLocation getAnimationResource(MeowmereItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
