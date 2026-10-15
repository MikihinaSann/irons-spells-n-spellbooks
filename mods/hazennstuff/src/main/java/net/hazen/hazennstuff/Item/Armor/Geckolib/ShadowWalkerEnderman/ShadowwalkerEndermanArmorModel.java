package net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ShadowwalkerEndermanArmorModel extends DefaultedEntityGeoModel<ShadowwalkerEndermanArmorItem> {
   public ShadowwalkerEndermanArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ShadowwalkerEndermanArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/shadowwalker_enderman_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowwalkerEndermanArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/shadowwalker_enderman_armor.png");
   }

   public ResourceLocation getAnimationResource(ShadowwalkerEndermanArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
