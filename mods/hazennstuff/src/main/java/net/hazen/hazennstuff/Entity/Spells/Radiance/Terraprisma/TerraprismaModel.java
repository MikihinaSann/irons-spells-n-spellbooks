package net.hazen.hazennstuff.Entity.Spells.Radiance.Terraprisma;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TerraprismaModel extends GeoModel<Terraprisma> {
   public ResourceLocation getModelResource(Terraprisma animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/terraprisma.geo.json");
   }

   public ResourceLocation getTextureResource(Terraprisma animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/terraprisma/terraprisma_0.png");
   }

   public ResourceLocation getAnimationResource(Terraprisma animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/terraprisma.animation.json");
   }
}
