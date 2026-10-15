package net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BoneHandsModel extends GeoModel<BoneHandsEntity> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/bone_hand.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/bone_hand.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/bone_hand.animation.json");

   public ResourceLocation getModelResource(BoneHandsEntity animatable) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(BoneHandsEntity animatable) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(BoneHandsEntity animatable) {
      return animationResource;
   }
}
