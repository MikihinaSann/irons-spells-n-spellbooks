package net.warphan.iss_magicfromtheeast.entity.spells.phantom_cavalry;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PhantomCavalryModel extends GeoModel<PhantomCavalryVisualEntity> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/phantom_cavalry.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/phantom_cavalry.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/cavalry.animation.json");

   public ResourceLocation getModelResource(PhantomCavalryVisualEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(PhantomCavalryVisualEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(PhantomCavalryVisualEntity animatable) {
      return animationResource;
   }
}
