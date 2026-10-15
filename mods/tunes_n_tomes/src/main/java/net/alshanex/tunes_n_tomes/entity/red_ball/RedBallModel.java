package net.alshanex.tunes_n_tomes.entity.red_ball;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedBallModel extends GeoModel<RedBallEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/sonic_boom_vfx.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/red_ball.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/red_ball.animation.json");

   public ResourceLocation getTextureResource(RedBallEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(RedBallEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(RedBallEntity animatable) {
      return ANIMS;
   }
}
