package net.alshanex.tunes_n_tomes.entity.red_arrow;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedArrowModel extends GeoModel<RedArrowEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/red_arrow.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/red_arrow.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/cadenza_combo_vfx.png");

   public ResourceLocation getModelResource(RedArrowEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(RedArrowEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(RedArrowEntity animatable) {
      return ANIMATIONS;
   }
}
