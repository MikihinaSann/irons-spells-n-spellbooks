package net.acetheeldritchking.discerning_the_eldritch.entity.spells.mourning_star;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MourningStarModel extends GeoModel<MourningStarProjectile> {
   public ResourceLocation getModelResource(MourningStarProjectile mourningStarProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/mourning_star_projectile.geo.json");
   }

   public ResourceLocation getTextureResource(MourningStarProjectile mourningStarProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/mourning_star/mourning_star_projectile.png");
   }

   public ResourceLocation getAnimationResource(MourningStarProjectile mourningStarProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/mourning_star_projectile.animation.json");
   }
}
