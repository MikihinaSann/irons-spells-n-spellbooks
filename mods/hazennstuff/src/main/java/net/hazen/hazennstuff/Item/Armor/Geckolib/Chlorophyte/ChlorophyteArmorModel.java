package net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ChlorophyteArmorModel extends DefaultedEntityGeoModel<ChlorophyteArmorItem> {
   public ChlorophyteArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ChlorophyteArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/chlorophyte_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ChlorophyteArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/chlorophyte_helmet_armor.png");
   }

   public ResourceLocation getAnimationResource(ChlorophyteArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
