package net.alshanex.alshanex_familiars.entity.misc;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueBirdEntityModel extends GeoModel<BlueBirdEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "textures/entity/blue_bird.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "geo/blue_bird.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "animations/blue_bird.animation.json");

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
