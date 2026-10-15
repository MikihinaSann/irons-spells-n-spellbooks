package net.alshanex.tunes_n_tomes.entity.blue_ball;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueBallModel extends GeoModel<BlueBallEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/sonic_boom_vfx.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/blue_ball.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/blue_ball.animation.json");

   public ResourceLocation getTextureResource(BlueBallEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(BlueBallEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(BlueBallEntity animatable) {
      return ANIMS;
   }
}
