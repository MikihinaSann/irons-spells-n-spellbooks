package net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.T1;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ProvocationT1Model extends DefaultedItemGeoModel<ProvocationT1> {
   public ProvocationT1Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ProvocationT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/provocation.geo.json");
   }

   public ResourceLocation getTextureResource(ProvocationT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/provocation.png");
   }

   public ResourceLocation getAnimationResource(ProvocationT1 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/provocation.animation.json");
   }
}
