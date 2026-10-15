package net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowScale;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ShadowScaleArmorModel extends DefaultedEntityGeoModel<ShadowScaleArmorItem> {
   public ShadowScaleArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ShadowScaleArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/shadow_scale_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowScaleArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/shadow_scale_armor.png");
   }

   public ResourceLocation getAnimationResource(ShadowScaleArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
