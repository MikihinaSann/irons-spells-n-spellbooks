package net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkA;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceChunkAModel extends GeoModel<IceChunkA> {
   public ResourceLocation getModelResource(IceChunkA animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/ice_chunk_a.geo.json");
   }

   public ResourceLocation getTextureResource(IceChunkA animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/ice_chunk_a.png");
   }

   public ResourceLocation getAnimationResource(IceChunkA animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/ice_chunk.animation.json");
   }
}
