package net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.T1;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class UmbranovaT1Model extends DefaultedItemGeoModel<UmbranovaT1> {
   public UmbranovaT1Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(UmbranovaT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/umbranova.geo.json");
   }

   public ResourceLocation getTextureResource(UmbranovaT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/umbranova.png");
   }

   public ResourceLocation getAnimationResource(UmbranovaT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/umbranova.animation.json");
   }
}
