package net.hazen.hazennstuff.Item.Armor.Geckolib.FleshMass;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshMassArmorModel extends DefaultedEntityGeoModel<FleshMassArmorItem> {
   public FleshMassArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FleshMassArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/flesh_mass_armor.geo.json");
   }

   public ResourceLocation getTextureResource(FleshMassArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/flesh_mass_armor.png");
   }

   public ResourceLocation getAnimationResource(FleshMassArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/flesh_mass_armor.animation.json");
   }
}
