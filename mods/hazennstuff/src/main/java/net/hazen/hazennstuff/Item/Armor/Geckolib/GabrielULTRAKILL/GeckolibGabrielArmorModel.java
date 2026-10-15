package net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibGabrielArmorModel extends DefaultedEntityGeoModel<GeckolibGabrielArmorItem> {
   public GeckolibGabrielArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibGabrielArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/gabriel_ultrakill_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibGabrielArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/gabriel_ultrakill_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibGabrielArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/gabriel_ultrakill_armor.animation.json");
   }
}
