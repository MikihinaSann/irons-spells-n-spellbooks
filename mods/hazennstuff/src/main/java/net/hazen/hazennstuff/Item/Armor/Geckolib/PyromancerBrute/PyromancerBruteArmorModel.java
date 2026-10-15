package net.hazen.hazennstuff.Item.Armor.Geckolib.PyromancerBrute;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PyromancerBruteArmorModel extends DefaultedEntityGeoModel<PyromancerBruteArmorItem> {
   public PyromancerBruteArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(PyromancerBruteArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/thunder_prowler_armor.geo.json");
   }

   public ResourceLocation getTextureResource(PyromancerBruteArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/thunder_prowler_armor.png");
   }

   public ResourceLocation getAnimationResource(PyromancerBruteArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
