package net.warphan.iss_magicfromtheeast.entity.spells.dragon_glide;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JadeLoongModel extends GeoModel<JadeLoong> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/jade_loong.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_loong.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/jade_loong.animation.json");

   public ResourceLocation getModelResource(JadeLoong object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(JadeLoong object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(JadeLoong animatable) {
      return animationResource;
   }
}
