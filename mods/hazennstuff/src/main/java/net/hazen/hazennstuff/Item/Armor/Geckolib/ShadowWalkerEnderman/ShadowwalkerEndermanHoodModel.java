package net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ShadowwalkerEndermanHoodModel extends DefaultedEntityGeoModel<ShadowwalkerEndermanHoodItem> {
   public ShadowwalkerEndermanHoodModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ShadowwalkerEndermanHoodItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/shadowwalker_enderman_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowwalkerEndermanHoodItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/shadowwalker_enderman_no_mask_armor.png");
   }

   public ResourceLocation getAnimationResource(ShadowwalkerEndermanHoodItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
