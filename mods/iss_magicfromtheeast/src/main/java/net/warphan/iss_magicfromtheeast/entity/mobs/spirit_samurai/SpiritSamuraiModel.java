package net.warphan.iss_magicfromtheeast.entity.mobs.spirit_samurai;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class SpiritSamuraiModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/spirit_revenant.png");
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/samurai.geo.json");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/samurai.animation.json");

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
