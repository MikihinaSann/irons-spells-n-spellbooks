package com.somake.somakespells.entity.model;

import com.somake.somakespells.entity.SovereignArmoryPortalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SovereignArmoryPortalModel extends GeoModel<SovereignArmoryPortalEntity> {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("somakespells", "geo/entity/sovereign_armory_portal.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/sovereign_armory_portal.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "animations/entity/sovereign_armory_portal.animation.json"
   );

   public ResourceLocation getModelResource(SovereignArmoryPortalEntity animatable) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(SovereignArmoryPortalEntity animatable) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(SovereignArmoryPortalEntity animatable) {
      return ANIMATION;
   }
}
