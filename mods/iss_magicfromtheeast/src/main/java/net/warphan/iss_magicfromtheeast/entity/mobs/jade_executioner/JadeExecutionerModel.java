package net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JadeExecutionerModel extends GeoModel<JadeExecutionerEntity> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/jade_executioner.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_executioner.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/jade_executioner.animation.json");

   public ResourceLocation getModelResource(JadeExecutionerEntity animatable) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(JadeExecutionerEntity animatable) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(JadeExecutionerEntity animatable) {
      return animationResource;
   }
}
