package net.alshanex.tunes_n_tomes.entity.blue_ball;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueBallHitModel extends GeoModel<BlueBallHitEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/blue_ball_hit.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/blue_ball_hit.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/sonic_boom_vfx.png");

   public ResourceLocation getModelResource(BlueBallHitEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(BlueBallHitEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(BlueBallHitEntity animatable) {
      return ANIMATIONS;
   }
}
