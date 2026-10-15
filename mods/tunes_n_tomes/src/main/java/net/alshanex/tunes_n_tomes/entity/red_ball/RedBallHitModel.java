package net.alshanex.tunes_n_tomes.entity.red_ball;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedBallHitModel extends GeoModel<RedBallHitEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/red_ball_hit.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/red_ball_hit.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/sonic_boom_vfx.png");

   public ResourceLocation getModelResource(RedBallHitEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(RedBallHitEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(RedBallHitEntity animatable) {
      return ANIMATIONS;
   }
}
