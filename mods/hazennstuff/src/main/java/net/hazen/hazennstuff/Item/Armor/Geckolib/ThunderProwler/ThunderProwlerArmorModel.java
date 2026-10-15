package net.hazen.hazennstuff.Item.Armor.Geckolib.ThunderProwler;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ThunderProwlerArmorModel extends DefaultedEntityGeoModel<ThunderProwlerArmorItem> {
   public ThunderProwlerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ThunderProwlerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/thunder_prowler_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ThunderProwlerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/thunder_prowler_armor.png");
   }

   public ResourceLocation getAnimationResource(ThunderProwlerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
