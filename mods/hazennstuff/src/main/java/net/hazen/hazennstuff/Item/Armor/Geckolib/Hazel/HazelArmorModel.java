package net.hazen.hazennstuff.Item.Armor.Geckolib.Hazel;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HazelArmorModel extends DefaultedEntityGeoModel<HazelArmorItem> {
   public HazelArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(HazelArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/nerfed_hazel_reprised_armor.geo.json");
   }

   public ResourceLocation getTextureResource(HazelArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/hazel.png");
   }

   public ResourceLocation getAnimationResource(HazelArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
