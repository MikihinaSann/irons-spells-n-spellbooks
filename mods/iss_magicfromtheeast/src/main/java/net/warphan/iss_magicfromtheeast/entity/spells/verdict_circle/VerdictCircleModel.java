package net.warphan.iss_magicfromtheeast.entity.spells.verdict_circle;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VerdictCircleModel extends GeoModel<VerdictCircle> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/verdict_circle.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/verdict_circle.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/verdict_circle.animation.json");

   public ResourceLocation getModelResource(VerdictCircle object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(VerdictCircle object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(VerdictCircle animatable) {
      return animationResource;
   }
}
