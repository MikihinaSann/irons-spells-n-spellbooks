package net.warphan.iss_magicfromtheeast.entity.spells.splitting_shuriken;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SplittingShurikenModel extends GeoModel<SplittingShurikenProjectile> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/spirit_shuriken.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/spirit_shuriken.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/shuriken.animation.json");

   public ResourceLocation getModelResource(SplittingShurikenProjectile object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(SplittingShurikenProjectile object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(SplittingShurikenProjectile animatable) {
      return animationResource;
   }
}
