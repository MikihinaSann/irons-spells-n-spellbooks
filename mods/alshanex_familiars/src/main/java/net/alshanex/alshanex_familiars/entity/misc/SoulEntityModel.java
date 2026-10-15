package net.alshanex.alshanex_familiars.entity.misc;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SoulEntityModel extends GeoModel<SoulEntity> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("alshanex_familiars", "textures/entity/pet_water.png");
   private static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/soul.geo.json");
   public static final ResourceLocation ANIMS = new ResourceLocation("alshanex_familiars", "animations/soul.animation.json");

   public ResourceLocation getTextureResource(SoulEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(SoulEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(SoulEntity animatable) {
      return ANIMS;
   }
}
