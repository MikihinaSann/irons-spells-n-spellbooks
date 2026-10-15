package net.hazen.hazennstuff.Entity.Spells.Astral.Evercomet;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EvercometModel extends GeoModel<Evercomet> {
   public ResourceLocation getModelResource(Evercomet animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/evercomet.geo.json");
   }

   public ResourceLocation getTextureResource(Evercomet animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/evercomet.png");
   }

   public ResourceLocation getAnimationResource(Evercomet animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/evercomet.animation.json");
   }
}
