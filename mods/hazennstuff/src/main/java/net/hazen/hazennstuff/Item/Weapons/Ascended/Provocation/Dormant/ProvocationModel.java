package net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.Dormant;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ProvocationModel extends DefaultedItemGeoModel<Provocation> {
   public ProvocationModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(Provocation animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/provocation.geo.json");
   }

   public ResourceLocation getTextureResource(Provocation animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/provocation.png");
   }

   public ResourceLocation getAnimationResource(Provocation animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/provocation.animation.json");
   }
}
