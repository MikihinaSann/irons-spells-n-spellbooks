package net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.Dormant;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class UmbranovaModel extends DefaultedItemGeoModel<Umbranova> {
   public UmbranovaModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(Umbranova animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/umbranova.geo.json");
   }

   public ResourceLocation getTextureResource(Umbranova animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/umbranova.png");
   }

   public ResourceLocation getAnimationResource(Umbranova animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/umbranova.animation.json");
   }
}
