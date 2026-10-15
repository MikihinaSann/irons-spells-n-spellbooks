package net.hazen.hazennstuff.Item.Weapons.Generic.Beongae;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BeongaeModel extends DefaultedItemGeoModel<BeongaeItem> {
   public BeongaeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(BeongaeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/beongae.geo.json");
   }

   public ResourceLocation getTextureResource(BeongaeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/beongae.png");
   }

   public ResourceLocation getAnimationResource(BeongaeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/item/weapons/beongae.animation.json");
   }
}
