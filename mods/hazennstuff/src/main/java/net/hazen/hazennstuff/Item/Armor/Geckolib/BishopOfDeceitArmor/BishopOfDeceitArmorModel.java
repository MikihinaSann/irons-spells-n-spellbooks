package net.hazen.hazennstuff.Item.Armor.Geckolib.BishopOfDeceitArmor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BishopOfDeceitArmorModel extends DefaultedEntityGeoModel<BishopOfDeceitArmorItem> {
   public BishopOfDeceitArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(BishopOfDeceitArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/bishop_of_deceit_armor.geo.json");
   }

   public ResourceLocation getTextureResource(BishopOfDeceitArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/bishop_of_deceit_armor.png");
   }

   public ResourceLocation getAnimationResource(BishopOfDeceitArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
