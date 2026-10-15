package net.hazen.hazennstuff.Entity.Spells.Nature.ThornChakram;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThornChakramModel extends GeoModel<ThornChakram> {
   public ResourceLocation getModelResource(ThornChakram animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/thorn_chakram_spell.geo.json");
   }

   public ResourceLocation getTextureResource(ThornChakram animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/thorn_chakram_spell.png");
   }

   public ResourceLocation getAnimationResource(ThornChakram animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/thorn_chakram_spell.animation.json");
   }
}
