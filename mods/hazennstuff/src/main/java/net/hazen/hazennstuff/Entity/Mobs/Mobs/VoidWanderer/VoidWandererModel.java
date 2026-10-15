package net.hazen.hazennstuff.Entity.Mobs.Mobs.VoidWanderer;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoidWandererModel extends GeoModel<VoidWanderer> {
   public ResourceLocation getTextureResource(VoidWanderer mob) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/void_wanderer.png");
   }

   public ResourceLocation getModelResource(VoidWanderer animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/mobs/void_wanderer.geo.json");
   }

   public ResourceLocation getAnimationResource(VoidWanderer animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/mobs/void_wanderer.animation.json");
   }
}
