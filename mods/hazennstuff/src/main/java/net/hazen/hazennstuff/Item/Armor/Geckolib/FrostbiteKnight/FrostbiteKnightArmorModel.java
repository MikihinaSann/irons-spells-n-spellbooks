package net.hazen.hazennstuff.Item.Armor.Geckolib.FrostbiteKnight;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FrostbiteKnightArmorModel extends DefaultedEntityGeoModel<FrostbiteKnightArmorItem> {
   public FrostbiteKnightArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FrostbiteKnightArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/frostbite_knight_armor.geo.json");
   }

   public ResourceLocation getTextureResource(FrostbiteKnightArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/frostbite_knight_armor.png");
   }

   public ResourceLocation getAnimationResource(FrostbiteKnightArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
