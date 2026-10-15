package net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler.Soul;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SoulLegionnaireRulerArmorModel extends DefaultedEntityGeoModel<SoulLegionnaireRulerArmorItem> {
   public SoulLegionnaireRulerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SoulLegionnaireRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/legionnaire_ruler_armor.geo.json");
   }

   public ResourceLocation getTextureResource(SoulLegionnaireRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/legionnaire_ruler_soul_armor.png");
   }

   public ResourceLocation getAnimationResource(SoulLegionnaireRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
