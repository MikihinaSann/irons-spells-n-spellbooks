package net.alshanex.tunes_n_tomes.entity.green_effect;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GreenEffectModel extends GeoModel<GreenEffectEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/green_effect.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/green_effect.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/cadenza_combo_vfx.png");

   public ResourceLocation getModelResource(GreenEffectEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(GreenEffectEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(GreenEffectEntity animatable) {
      return ANIMATIONS;
   }
}
