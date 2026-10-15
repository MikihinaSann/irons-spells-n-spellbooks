package net.hazen.hazennstuff.Entity.Spells.Nature.SpiderLily;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpiderLilyModel extends GeoModel<SpiderLily> {
   public ResourceLocation getModelResource(SpiderLily animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/spider_lily.geo.json");
   }

   public ResourceLocation getTextureResource(SpiderLily animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/spider_lily.png");
   }

   public ResourceLocation getAnimationResource(SpiderLily animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/spider_lily.animation.json");
   }
}
