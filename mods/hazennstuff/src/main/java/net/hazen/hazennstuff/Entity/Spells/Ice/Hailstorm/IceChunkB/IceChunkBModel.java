package net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkB;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceChunkBModel extends GeoModel<IceChunkB> {
   public ResourceLocation getModelResource(IceChunkB animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/ice_chunk_b.geo.json");
   }

   public ResourceLocation getTextureResource(IceChunkB animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/ice_chunk_b.png");
   }

   public ResourceLocation getAnimationResource(IceChunkB animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/ice_chunk.animation.json");
   }
}
