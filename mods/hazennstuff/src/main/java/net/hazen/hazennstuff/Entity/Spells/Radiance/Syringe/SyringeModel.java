package net.hazen.hazennstuff.Entity.Spells.Radiance.Syringe;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SyringeModel extends GeoModel<Syringe> {
   public ResourceLocation getModelResource(Syringe animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/syringe.geo.json");
   }

   public ResourceLocation getTextureResource(Syringe animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/syringe_alt.png");
   }

   public ResourceLocation getAnimationResource(Syringe animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/syringe.animation.json");
   }
}
