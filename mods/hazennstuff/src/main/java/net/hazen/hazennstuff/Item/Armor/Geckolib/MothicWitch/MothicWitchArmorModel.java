package net.hazen.hazennstuff.Item.Armor.Geckolib.MothicWitch;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MothicWitchArmorModel extends DefaultedEntityGeoModel<MothicWitchArmorItem> {
   public MothicWitchArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MothicWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/mothic_witch_armor.geo.json");
   }

   public ResourceLocation getTextureResource(MothicWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/mothic_witch_armor.png");
   }

   public ResourceLocation getAnimationResource(MothicWitchArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
