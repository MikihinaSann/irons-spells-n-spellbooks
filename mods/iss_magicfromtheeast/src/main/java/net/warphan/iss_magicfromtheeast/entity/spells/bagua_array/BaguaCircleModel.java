package net.warphan.iss_magicfromtheeast.entity.spells.bagua_array;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BaguaCircleModel extends GeoModel<BaguaCircle> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/bagua_circle.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/bagua_circle.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/bagua_circle.animation.json");

   public ResourceLocation getModelResource(BaguaCircle object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(BaguaCircle object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(BaguaCircle animatable) {
      return animationResource;
   }
}
