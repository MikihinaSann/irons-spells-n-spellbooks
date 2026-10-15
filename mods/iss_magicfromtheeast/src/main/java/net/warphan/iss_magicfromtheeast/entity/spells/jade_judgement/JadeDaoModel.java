package net.warphan.iss_magicfromtheeast.entity.spells.jade_judgement;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JadeDaoModel extends GeoModel<JadeDao> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/jade_dao.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_dao.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/jade_dao_animations.json");

   public ResourceLocation getModelResource(JadeDao object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(JadeDao object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(JadeDao animatable) {
      return animationResource;
   }
}
