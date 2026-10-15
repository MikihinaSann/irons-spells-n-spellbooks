package net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.CinderousFurnace;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class CinderousFurnaceModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/cinderous_furnace_armor.png");
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/mobs/cinderous_furnace.geo.json");
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(
      "hazennstuff", "animations/entities/mobs/cinderous_furnace.animation.json"
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
