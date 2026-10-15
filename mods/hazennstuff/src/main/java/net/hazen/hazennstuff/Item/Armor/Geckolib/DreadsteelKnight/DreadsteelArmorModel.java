package net.hazen.hazennstuff.Item.Armor.Geckolib.DreadsteelKnight;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DreadsteelArmorModel extends DefaultedEntityGeoModel<DreadsteelKnightArmorItem> {
   public DreadsteelArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(DreadsteelKnightArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/dreadsteel_knight_armor.geo.json");
   }

   public ResourceLocation getTextureResource(DreadsteelKnightArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/dreadsteel_knight_armor.png");
   }

   public ResourceLocation getAnimationResource(DreadsteelKnightArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
