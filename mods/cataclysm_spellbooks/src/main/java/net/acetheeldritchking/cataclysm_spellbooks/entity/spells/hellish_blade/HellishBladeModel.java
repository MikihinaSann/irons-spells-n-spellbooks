package net.acetheeldritchking.cataclysm_spellbooks.entity.spells.hellish_blade;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HellishBladeModel extends GeoModel<HellishBladeProjectile> {
   public ResourceLocation getModelResource(HellishBladeProjectile object) {
      return ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/hellish_blade.geo.json");
   }

   public ResourceLocation getTextureResource(HellishBladeProjectile object) {
      return object.getIsSoul()
         ? ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/entity/hellish_blade/hellish_blade_soul.png")
         : ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/entity/hellish_blade/hellish_blade.png");
   }

   public ResourceLocation getAnimationResource(HellishBladeProjectile animatable) {
      return ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "animations/entity/hellish_blade.animation.json");
   }
}
