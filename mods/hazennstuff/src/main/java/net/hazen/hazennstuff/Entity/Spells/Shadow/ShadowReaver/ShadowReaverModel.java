package net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowReaver;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadowReaverModel extends GeoModel<ShadowReaver> {
   public ResourceLocation getModelResource(ShadowReaver animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/shadow_reaver.geo.json");
   }

   public ResourceLocation getTextureResource(ShadowReaver animatable) {
      int frameCount = 6;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/shadow_reaver_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(ShadowReaver animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/shadow_reaver.animation.json");
   }
}
