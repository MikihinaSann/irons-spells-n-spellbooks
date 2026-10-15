package net.hazen.hazennstuff.Item.Armor.Geckolib.Hazel;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HazelHOLYMOLYArmorModel extends DefaultedEntityGeoModel<HazelHOLYMOLYArmorItem> {
   public HazelHOLYMOLYArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(HazelHOLYMOLYArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/hazel_reprised.geo.json");
   }

   public ResourceLocation getTextureResource(HazelHOLYMOLYArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/hazel.png");
   }

   public ResourceLocation getAnimationResource(HazelHOLYMOLYArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
