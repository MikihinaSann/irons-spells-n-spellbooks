package net.hazen.hazennstuff.Entity.Spells.Evocation.SpectralAxe;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpectralAxeModel extends GeoModel<SpectralAxe> {
   public ResourceLocation getModelResource(SpectralAxe animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/spectral_axe.geo.json");
   }

   public ResourceLocation getTextureResource(SpectralAxe animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/spectral_axe.png");
   }

   public ResourceLocation getAnimationResource(SpectralAxe animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/spectral_axe.animation.json");
   }
}
