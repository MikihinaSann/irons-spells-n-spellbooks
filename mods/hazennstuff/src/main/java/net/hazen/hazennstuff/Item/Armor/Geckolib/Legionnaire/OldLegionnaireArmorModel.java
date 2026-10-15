package net.hazen.hazennstuff.Item.Armor.Geckolib.Legionnaire;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OldLegionnaireArmorModel extends DefaultedEntityGeoModel<OldLegionnaireArmorItem> {
   public OldLegionnaireArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(OldLegionnaireArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/legionnaire_armor.geo.json");
   }

   public ResourceLocation getTextureResource(OldLegionnaireArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/legionnaire_armor.png");
   }

   public ResourceLocation getAnimationResource(OldLegionnaireArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
