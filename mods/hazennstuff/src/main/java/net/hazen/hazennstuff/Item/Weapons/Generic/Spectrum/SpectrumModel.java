package net.hazen.hazennstuff.Item.Weapons.Generic.Spectrum;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class SpectrumModel extends DefaultedItemGeoModel<SpectrumItem> {
   public SpectrumModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SpectrumItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/spectrum.geo.json");
   }

   public ResourceLocation getTextureResource(SpectrumItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/spectrum.png");
   }

   public ResourceLocation getAnimationResource(SpectrumItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
