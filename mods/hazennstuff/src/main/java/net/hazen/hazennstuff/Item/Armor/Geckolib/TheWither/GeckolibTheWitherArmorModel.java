package net.hazen.hazennstuff.Item.Armor.Geckolib.TheWither;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibTheWitherArmorModel extends DefaultedEntityGeoModel<GeckolibTheWitherArmorItem> {
   public GeckolibTheWitherArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibTheWitherArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/the_wither_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibTheWitherArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/the_wither_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibTheWitherArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/the_wither_armor.animation.json");
   }
}
