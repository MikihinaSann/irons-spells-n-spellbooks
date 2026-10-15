package net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CataclysmBladeBigModel extends GeoModel<CataclysmBladeBigProjectile> {
   public ResourceLocation getModelResource(CataclysmBladeBigProjectile cataclysmBladeBigProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/cataclysm_blade_big_projectile.geo.json");
   }

   public ResourceLocation getTextureResource(CataclysmBladeBigProjectile cataclysmBladeBigProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/cataclysm/cataclysm_big.png");
   }

   public ResourceLocation getAnimationResource(CataclysmBladeBigProjectile cataclysmBladeBigProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/cataclysm_blade_projectile.animation.json");
   }
}
