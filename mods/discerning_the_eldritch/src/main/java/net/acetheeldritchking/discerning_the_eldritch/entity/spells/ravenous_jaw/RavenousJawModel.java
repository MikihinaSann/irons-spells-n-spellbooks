package net.acetheeldritchking.discerning_the_eldritch.entity.spells.ravenous_jaw;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RavenousJawModel extends GeoModel<RavenousJawEntity> {
   public ResourceLocation getModelResource(RavenousJawEntity ravenousJawEntity) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/ravenous_jaw.geo.json");
   }

   public ResourceLocation getTextureResource(RavenousJawEntity ravenousJawEntity) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/ravenous_jaw/ravenous_jaw.png");
   }

   public ResourceLocation getAnimationResource(RavenousJawEntity ravenousJawEntity) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/ravenous_jaw.animation.json");
   }
}
