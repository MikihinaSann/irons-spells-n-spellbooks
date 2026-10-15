package net.alshanex.tunes_n_tomes.entity.hymn_of_hope;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HopeBurstModel extends GeoModel<HopeBurstEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/hymn_of_hope.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/hope_burst.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/melodic_shield_vfx.png");

   public ResourceLocation getModelResource(HopeBurstEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(HopeBurstEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(HopeBurstEntity animatable) {
      return ANIMATIONS;
   }
}
