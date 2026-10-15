package net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ShadowwalkerEndermanBlindfoldModel extends DefaultedEntityGeoModel<ShadowwalkerEndermanBlindfoldItem> {
   public ShadowwalkerEndermanBlindfoldModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ShadowwalkerEndermanBlindfoldItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/shadowwalker_enderman_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowwalkerEndermanBlindfoldItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/shadowwalker_enderman_blindfold_armor.png");
   }

   public ResourceLocation getAnimationResource(ShadowwalkerEndermanBlindfoldItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
