package net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.T2;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ProvocationT2Model extends DefaultedItemGeoModel<ProvocationT2> {
   public ProvocationT2Model() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ProvocationT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/provocation.geo.json");
   }

   public ResourceLocation getTextureResource(ProvocationT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/provocation.png");
   }

   public ResourceLocation getAnimationResource(ProvocationT2 animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/provocation.animation.json");
   }
}
