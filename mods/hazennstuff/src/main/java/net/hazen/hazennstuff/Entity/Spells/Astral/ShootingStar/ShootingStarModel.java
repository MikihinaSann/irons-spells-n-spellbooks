package net.hazen.hazennstuff.Entity.Spells.Astral.ShootingStar;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShootingStarModel extends GeoModel<ShootingStar> {
   public ResourceLocation getModelResource(ShootingStar animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/shooting_star.geo.json");
   }

   public ResourceLocation getTextureResource(ShootingStar animatable) {
      int frameCount = 3;
      int frameDuration = 1;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/shooting_star_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(ShootingStar animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/shooting_star.animation.json");
   }
}
