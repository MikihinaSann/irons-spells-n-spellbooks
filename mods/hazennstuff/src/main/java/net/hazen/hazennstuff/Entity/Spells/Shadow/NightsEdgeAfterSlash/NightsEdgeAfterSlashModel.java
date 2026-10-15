package net.hazen.hazennstuff.Entity.Spells.Shadow.NightsEdgeAfterSlash;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NightsEdgeAfterSlashModel extends GeoModel<NightsEdgeAfterSlash> {
   public ResourceLocation getModelResource(NightsEdgeAfterSlash animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/nights_edge_after_slash.geo.json");
   }

   public ResourceLocation getTextureResource(NightsEdgeAfterSlash animatable) {
      int frameCount = 8;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/nights_edge_after_slash_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(NightsEdgeAfterSlash animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/nights_edge_after_slash.animation.json");
   }
}
