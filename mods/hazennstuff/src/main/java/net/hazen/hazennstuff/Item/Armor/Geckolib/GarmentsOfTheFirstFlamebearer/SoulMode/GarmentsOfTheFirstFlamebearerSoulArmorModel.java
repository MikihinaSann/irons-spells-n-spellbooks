package net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.SoulMode;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GarmentsOfTheFirstFlamebearerSoulArmorModel extends DefaultedEntityGeoModel<GarmentsOfTheFirstFlamebearerSoulArmorItem> {
   public GarmentsOfTheFirstFlamebearerSoulArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GarmentsOfTheFirstFlamebearerSoulArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/garments_of_the_first_flamebearer_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GarmentsOfTheFirstFlamebearerSoulArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/garments_of_the_first_flamebearer_soul_armor.png");
   }

   public ResourceLocation getAnimationResource(GarmentsOfTheFirstFlamebearerSoulArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
