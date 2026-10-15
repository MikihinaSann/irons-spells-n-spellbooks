package net.alshanex.tunes_n_tomes.entity.red_circle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedCircleModel extends GeoModel<RedCircleEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/red_circle.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/red_circle.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/chord_cycle_vfx.png");

   public ResourceLocation getModelResource(RedCircleEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(RedCircleEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(RedCircleEntity animatable) {
      return ANIMATIONS;
   }
}
