package net.hazen.hazennstuff.Item.Armor.Geckolib.FrostbiteHunter;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FrostbiteHunterArmorModel extends DefaultedEntityGeoModel<FrostbiteHunterArmorItem> {
   public FrostbiteHunterArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(FrostbiteHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/frostbite_hunter_armor.geo.json");
   }

   public ResourceLocation getTextureResource(FrostbiteHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/frostbite_hunter_armor.png");
   }

   public ResourceLocation getAnimationResource(FrostbiteHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
