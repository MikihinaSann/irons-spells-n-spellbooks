package net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class LegionnaireRulerArmorModel extends DefaultedEntityGeoModel<LegionnaireRulerArmorItem> {
   public LegionnaireRulerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(LegionnaireRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/legionnaire_ruler_armor.geo.json");
   }

   public ResourceLocation getTextureResource(LegionnaireRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/legionnaire_ruler_armor.png");
   }

   public ResourceLocation getAnimationResource(LegionnaireRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
