package net.hazen.hazennstuff.Item.Armor.Geckolib.SupremeWitch;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibSupremeWitchArmorModel extends DefaultedEntityGeoModel<GeckolibSupremeWitchArmorItem> {
   public GeckolibSupremeWitchArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibSupremeWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/supreme_witch_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibSupremeWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/supreme_witch_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibSupremeWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
