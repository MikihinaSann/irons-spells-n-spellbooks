package net.hazen.hazennstuff.Entity.Spells.Eldritch.SoulSeeker;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SoulSeekerModel extends GeoModel<SoulSeeker> {
   public ResourceLocation getModelResource(SoulSeeker animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/soul_seeker.geo.json");
   }

   public ResourceLocation getTextureResource(SoulSeeker animatable) {
      int frameCount = 4;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/soul_seeker_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(SoulSeeker animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/spark.animation.json");
   }
}
