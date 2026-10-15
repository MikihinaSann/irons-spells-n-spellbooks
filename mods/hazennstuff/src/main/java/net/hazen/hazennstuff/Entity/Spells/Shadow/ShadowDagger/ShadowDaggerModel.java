package net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowDagger;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadowDaggerModel extends GeoModel<ShadowDagger> {
   public ResourceLocation getModelResource(ShadowDagger animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/shadow_dagger.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowDagger animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/shadow_dagger.png");
   }

   public ResourceLocation getAnimationResource(ShadowDagger animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/spark.animation.json");
   }
}
