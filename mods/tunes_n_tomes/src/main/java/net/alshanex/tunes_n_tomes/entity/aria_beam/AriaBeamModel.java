package net.alshanex.tunes_n_tomes.entity.aria_beam;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AriaBeamModel extends GeoModel<AriaBeamEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/aria_beam.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/aria_beam.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/grand_performance_vfx.png");

   public ResourceLocation getModelResource(AriaBeamEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(AriaBeamEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(AriaBeamEntity animatable) {
      return ANIMATIONS;
   }
}
