package net.hazen.hazennstuff.Item.Armor.Geckolib.AbberantPredator;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AbberantPredatorArmorModel extends DefaultedEntityGeoModel<AbberantPredatorArmorItem> {
   public AbberantPredatorArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AbberantPredatorArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/abberant_predator_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AbberantPredatorArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/abberant_predator_armor.png");
   }

   public ResourceLocation getAnimationResource(AbberantPredatorArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
