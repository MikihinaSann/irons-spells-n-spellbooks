package net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkC;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceChunkCModel extends GeoModel<IceChunkC> {
   public ResourceLocation getModelResource(IceChunkC animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/ice_chunk_c.geo.json");
   }

   public ResourceLocation getTextureResource(IceChunkC animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/ice_chunk_c.png");
   }

   public ResourceLocation getAnimationResource(IceChunkC animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/ice_chunk.animation.json");
   }
}
