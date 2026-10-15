package net.hazen.hazennstuff.Item.Armor.Geckolib.Infestation;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibInfestationArmorModel extends DefaultedEntityGeoModel<GeckolibInfestationArmorItem> {
   public GeckolibInfestationArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibInfestationArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/infestation_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibInfestationArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/infestation_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibInfestationArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/infestation_armor_animation.json");
   }
}
