package net.hazen.hazennstuff.Entity.Spells.Blood.LifestealKnife;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LifestealKnifeModel extends GeoModel<VampireKnife> {
   public ResourceLocation getModelResource(VampireKnife animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/vampire_knife_projectile.geo.json");
   }

   public ResourceLocation getTextureResource(VampireKnife animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/vampire_knife_projectile.png");
   }

   public ResourceLocation getAnimationResource(VampireKnife animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/vampire_knife_projectile.animation.json");
   }
}
