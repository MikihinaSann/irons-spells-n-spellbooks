package net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireCommander;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class LegionnaireCommanderArmorModel extends DefaultedEntityGeoModel<LegionnaireCommanderArmorItem> {
   public LegionnaireCommanderArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(LegionnaireCommanderArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/legionnaire_commander_armor.geo.json");
   }

   public ResourceLocation getTextureResource(LegionnaireCommanderArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/legionnaire_commander_armor.png");
   }

   public ResourceLocation getAnimationResource(LegionnaireCommanderArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
