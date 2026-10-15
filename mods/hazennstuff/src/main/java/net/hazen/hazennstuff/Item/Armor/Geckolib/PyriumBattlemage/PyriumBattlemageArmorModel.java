package net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumBattlemage;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PyriumBattlemageArmorModel extends DefaultedEntityGeoModel<PyriumBattlemageArmorItem> {
   public PyriumBattlemageArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(PyriumBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/pyrium_battlemage_armor.geo.json");
   }

   public ResourceLocation getTextureResource(PyriumBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/pyrium_battlemage_armor.png");
   }

   public ResourceLocation getAnimationResource(PyriumBattlemageArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
