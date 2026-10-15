package net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.TheInferno;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class TheInfernoModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/the_inferno_armor.png");
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/mobs/the_inferno.geo.json");
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(
      "hazennstuff", "animations/entities/mobs/the_inferno.animation.json"
   );

   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return textureResource;
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return modelResource;
   }

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
      return animationResource;
   }
}
