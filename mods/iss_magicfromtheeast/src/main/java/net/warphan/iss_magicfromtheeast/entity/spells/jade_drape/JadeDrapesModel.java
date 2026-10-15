package net.warphan.iss_magicfromtheeast.entity.spells.jade_drape;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JadeDrapesModel extends GeoModel<JadeDrapesEntity> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/jade_drape_entity.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_drape.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/jade_drape.animation.json");

   public ResourceLocation getModelResource(JadeDrapesEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(JadeDrapesEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(JadeDrapesEntity animatable) {
      return animationResource;
   }
}
