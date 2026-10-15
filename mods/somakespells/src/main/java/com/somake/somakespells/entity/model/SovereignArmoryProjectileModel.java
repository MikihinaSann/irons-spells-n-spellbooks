package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.SovereignArmoryProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SovereignArmoryProjectileModel extends GeoModel<SovereignArmoryProjectileEntity> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/entity/sovereign_armory_projectile.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/sovereign_armory_projectile.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "animations/entity/sovereign_armory_projectile.animation.json"
   );

   public ResourceLocation getModelResource(SovereignArmoryProjectileEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(SovereignArmoryProjectileEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(SovereignArmoryProjectileEntity animatable) {
      return ANIMATION;
   }
}
