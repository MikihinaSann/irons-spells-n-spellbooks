package net.hazen.hazennstuff.Entity.Spells.Astral.CosmicBolt;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CosmicBoltModel extends GeoModel<CosmicBolt> {
   public ResourceLocation getModelResource(CosmicBolt animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/cosmic_bolt.geo.json");
   }

   public ResourceLocation getTextureResource(CosmicBolt animatable) {
      int frameCount = 8;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/cosmic_bolt_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(CosmicBolt animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/cosmic_bolt.animation.json");
   }
}
