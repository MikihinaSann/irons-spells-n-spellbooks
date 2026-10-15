package net.alshanex.tunes_n_tomes.entity.fortissimo;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FortissimoChargeModel extends GeoModel<FortissimoChargeEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/fortissimo.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/fortissimo_charge.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/grand_performance_vfx.png");

   public ResourceLocation getModelResource(FortissimoChargeEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(FortissimoChargeEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(FortissimoChargeEntity animatable) {
      return ANIMATIONS;
   }
}
