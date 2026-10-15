package net.hazen.hazennstuff.Item.Armor.Geckolib.SoulFlame;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibSoulFlameArmorModel extends DefaultedEntityGeoModel<GeckolibSoulFlameArmorItem> {
   public GeckolibSoulFlameArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibSoulFlameArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/soul_flame_armor_geckolib.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibSoulFlameArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/soul_flame_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibSoulFlameArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
