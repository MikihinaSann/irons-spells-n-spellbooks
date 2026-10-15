package net.alshanex.tunes_n_tomes.entity.red_ball;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedBallEffectModel extends GeoModel<RedBallEffectEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/red_ball_effect.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/red_ball_effect.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/cadenza_combo_vfx.png");

   public ResourceLocation getModelResource(RedBallEffectEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(RedBallEffectEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(RedBallEffectEntity animatable) {
      return ANIMATIONS;
   }
}
