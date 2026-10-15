package net.alshanex.tunes_n_tomes.entity.celestial_chant;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AngelEntityModel extends GeoModel<AngelEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/cupid_merged.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/cupid.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/cupid.animation.json");

   public ResourceLocation getTextureResource(AngelEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(AngelEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(AngelEntity animatable) {
      return ANIMS;
   }
}
