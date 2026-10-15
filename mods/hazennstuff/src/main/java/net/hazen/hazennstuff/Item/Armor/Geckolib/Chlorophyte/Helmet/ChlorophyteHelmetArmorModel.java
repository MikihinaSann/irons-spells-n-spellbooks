package net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Helmet;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ChlorophyteHelmetArmorModel extends DefaultedEntityGeoModel<ChlorophyteHelmetArmorItem> {
   public ChlorophyteHelmetArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ChlorophyteHelmetArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/chlorophyte_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ChlorophyteHelmetArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/chlorophyte_helmet_armor.png");
   }

   public ResourceLocation getAnimationResource(ChlorophyteHelmetArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
