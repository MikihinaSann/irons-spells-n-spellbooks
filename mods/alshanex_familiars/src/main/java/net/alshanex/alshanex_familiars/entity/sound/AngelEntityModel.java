package net.alshanex.alshanex_familiars.entity.sound;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AngelEntityModel extends GeoModel<AngelEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "textures/entity/cupid_merged.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "geo/cupid.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "animations/cupid.animation.json");

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
