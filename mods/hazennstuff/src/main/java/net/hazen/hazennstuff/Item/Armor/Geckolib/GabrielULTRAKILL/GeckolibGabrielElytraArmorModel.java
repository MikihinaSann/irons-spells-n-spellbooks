package net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibGabrielElytraArmorModel extends DefaultedEntityGeoModel<GeckolibGabrielElytraItem> {
   public GeckolibGabrielElytraArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibGabrielElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/gabriel_ultrakill_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibGabrielElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/gabriel_ultrakill_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibGabrielElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/gabriel_ultrakill_armor.animation.json");
   }
}
