package net.alshanex.tunes_n_tomes.entity.blue_ball;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueBallEffectModel extends GeoModel<BlueBallEffectEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/blue_effect.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/blue_effect.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/cadenza_combo_vfx.png");

   public ResourceLocation getModelResource(BlueBallEffectEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(BlueBallEffectEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(BlueBallEffectEntity animatable) {
      return ANIMATIONS;
   }
}
