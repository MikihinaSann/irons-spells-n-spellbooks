package net.alshanex.tunes_n_tomes.entity.blue_circle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueCircleModel extends GeoModel<BlueCircleEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/blue_circle.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/blue_circle.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/chord_cycle_vfx.png");

   public ResourceLocation getModelResource(BlueCircleEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(BlueCircleEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(BlueCircleEntity animatable) {
      return ANIMATIONS;
   }
}
