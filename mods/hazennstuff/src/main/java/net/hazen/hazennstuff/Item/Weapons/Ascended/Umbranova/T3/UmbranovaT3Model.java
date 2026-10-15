package net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.T3;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class UmbranovaT3Model extends DefaultedItemGeoModel<UmbranovaT3> {
   public UmbranovaT3Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(UmbranovaT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/umbranova.geo.json");
   }

   public ResourceLocation getTextureResource(UmbranovaT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/umbranova.png");
   }

   public ResourceLocation getAnimationResource(UmbranovaT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/umbranova.animation.json");
   }
}
