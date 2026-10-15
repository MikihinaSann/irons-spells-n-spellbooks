package net.hazen.hazennstuff.Item.Weapons.Generic.IcePike;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class IcePikeModel extends DefaultedItemGeoModel<IcePikeItem> {
   public IcePikeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(IcePikeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ice_pike.geo.json");
   }

   public ResourceLocation getTextureResource(IcePikeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ice_pike.png");
   }

   public ResourceLocation getAnimationResource(IcePikeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
