package net.hazen.hazennstuff.Item.Armor.Geckolib.MothicWitch;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NerfedMothicWitchArmorModel extends DefaultedEntityGeoModel<NerfedMothicWitchArmorItem> {
   public NerfedMothicWitchArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(NerfedMothicWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/mothic_witch_armor.geo.json");
   }

   public ResourceLocation getTextureResource(NerfedMothicWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/nerfed_mothic_witch_armor.png");
   }

   public ResourceLocation getAnimationResource(NerfedMothicWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
