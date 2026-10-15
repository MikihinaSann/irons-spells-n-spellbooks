package net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.T3;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ProvocationT3Model extends DefaultedItemGeoModel<ProvocationT3> {
   public ProvocationT3Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ProvocationT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/provocation.geo.json");
   }

   public ResourceLocation getTextureResource(ProvocationT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/provocation.png");
   }

   public ResourceLocation getAnimationResource(ProvocationT3 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/provocation.animation.json");
   }
}
