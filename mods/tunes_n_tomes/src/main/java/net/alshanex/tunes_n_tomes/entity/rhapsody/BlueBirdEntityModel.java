package net.alshanex.tunes_n_tomes.entity.rhapsody;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueBirdEntityModel extends GeoModel<BlueBirdEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/blue_bird.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/blue_bird.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/blue_bird.animation.json");

   public ResourceLocation getTextureResource(BlueBirdEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(BlueBirdEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(BlueBirdEntity animatable) {
      return ANIMS;
   }
}
