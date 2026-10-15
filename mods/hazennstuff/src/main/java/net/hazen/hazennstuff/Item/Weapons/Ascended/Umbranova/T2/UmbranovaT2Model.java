package net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.T2;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class UmbranovaT2Model extends DefaultedItemGeoModel<UmbranovaT2> {
   public UmbranovaT2Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(UmbranovaT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/umbranova.geo.json");
   }

   public ResourceLocation getTextureResource(UmbranovaT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/umbranova.png");
   }

   public ResourceLocation getAnimationResource(UmbranovaT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/umbranova.animation.json");
   }
}
