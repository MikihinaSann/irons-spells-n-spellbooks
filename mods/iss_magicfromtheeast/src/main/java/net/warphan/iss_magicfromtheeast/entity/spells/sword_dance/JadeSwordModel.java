package net.warphan.iss_magicfromtheeast.entity.spells.sword_dance;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JadeSwordModel extends GeoModel<JadeSword> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/jade_sword.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_sword.png");

   public ResourceLocation getModelResource(JadeSword object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(JadeSword object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(JadeSword animatable) {
      return null;
   }
}
