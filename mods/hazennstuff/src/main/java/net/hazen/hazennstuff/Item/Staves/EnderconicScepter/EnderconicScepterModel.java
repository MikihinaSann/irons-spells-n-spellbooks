package net.hazen.hazennstuff.Item.Staves.EnderconicScepter;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EnderconicScepterModel extends DefaultedItemGeoModel<EnderconicScepter> {
   public EnderconicScepterModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(EnderconicScepter animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/enderconic_scepter.geo.json");
   }

   public ResourceLocation getTextureResource(EnderconicScepter animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/enderconic_scepter.png");
   }

   public ResourceLocation getAnimationResource(EnderconicScepter animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/staves/enderconic_scepter.animation.json");
   }
}
