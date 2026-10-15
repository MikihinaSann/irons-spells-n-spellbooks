package net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Headgear;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ChlorophyteHeadgearArmorModel extends DefaultedEntityGeoModel<ChlorophyteHeadgearArmorItem> {
   public ChlorophyteHeadgearArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ChlorophyteHeadgearArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/chlorophyte_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ChlorophyteHeadgearArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/chlorophyte_headgear_armor.png");
   }

   public ResourceLocation getAnimationResource(ChlorophyteHeadgearArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
