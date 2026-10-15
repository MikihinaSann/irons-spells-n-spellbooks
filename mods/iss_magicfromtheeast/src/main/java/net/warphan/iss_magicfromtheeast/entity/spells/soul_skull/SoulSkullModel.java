package net.warphan.iss_magicfromtheeast.entity.spells.soul_skull;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SoulSkullModel extends GeoModel<SoulSkullProjectile> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/soul_skull.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/soul_skull.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/soul_skull.animation.json");

   public ResourceLocation getModelResource(SoulSkullProjectile object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(SoulSkullProjectile object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(SoulSkullProjectile animatable) {
      return animationResource;
   }
}
