package net.hazen.hazennstuff.Entity.Spells.Fire.ImmolatingObliterator;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ScorchingSlashModel extends GeoModel<ScorchingSlash> {
   public ResourceLocation getModelResource(ScorchingSlash animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/nights_edge_after_slash.geo.json");
   }

   public ResourceLocation getTextureResource(ScorchingSlash animatable) {
      int frameCount = 4;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/immolating_obliterator_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(ScorchingSlash animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/nights_edge_after_slash.animation.json");
   }
}
