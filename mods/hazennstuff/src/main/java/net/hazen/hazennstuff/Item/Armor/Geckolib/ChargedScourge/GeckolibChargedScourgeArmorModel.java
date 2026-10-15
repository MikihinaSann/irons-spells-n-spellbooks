package net.hazen.hazennstuff.Item.Armor.Geckolib.ChargedScourge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibChargedScourgeArmorModel extends DefaultedEntityGeoModel<GeckolibChargedScourgeArmorItem> {
   public GeckolibChargedScourgeArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibChargedScourgeArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/charged_scourge_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibChargedScourgeArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/charged_scourge_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibChargedScourgeArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
