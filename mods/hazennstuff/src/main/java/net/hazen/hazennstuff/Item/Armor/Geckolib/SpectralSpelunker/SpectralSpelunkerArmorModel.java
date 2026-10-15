package net.hazen.hazennstuff.Item.Armor.Geckolib.SpectralSpelunker;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SpectralSpelunkerArmorModel extends DefaultedEntityGeoModel<SpectralSpelunkerArmorItem> {
   public SpectralSpelunkerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(SpectralSpelunkerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/spectral_spelunker_armor.geo.json");
   }

   public ResourceLocation getTextureResource(SpectralSpelunkerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/spectral_spelunker_armor.png");
   }

   public ResourceLocation getAnimationResource(SpectralSpelunkerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
