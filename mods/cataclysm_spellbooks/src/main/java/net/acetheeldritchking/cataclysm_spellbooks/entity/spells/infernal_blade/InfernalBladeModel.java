package net.acetheeldritchking.cataclysm_spellbooks.entity.spells.infernal_blade;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class InfernalBladeModel extends GeoModel<InfernalBladeProjectile> {
   public ResourceLocation getModelResource(InfernalBladeProjectile object) {
      return ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/infernal_blade_small.geo.json");
   }

   public ResourceLocation getTextureResource(InfernalBladeProjectile object) {
      return object.getIsSoul()
         ? ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/entity/infernal_blade_small/infernal_blade_small_soul.png")
         : ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/entity/infernal_blade_small/infernal_blade_small.png");
   }

   public ResourceLocation getAnimationResource(InfernalBladeProjectile animatable) {
      return ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "animations/entity/infernal_blade_small.animation.json");
   }
}
