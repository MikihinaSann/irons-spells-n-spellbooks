package net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class SpiritAshigaruModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/ashigaru.png");
   public static final ResourceLocation modelMeleeResource = new ResourceLocation("iss_magicfromtheeast", "geo/ashigaru_melee.geo.json");
   public static final ResourceLocation modelRangeResource = new ResourceLocation("iss_magicfromtheeast", "geo/ashigaru_range.geo.json");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/ashigaru.animation.json");

   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return textureResource;
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return object instanceof SpiritAshigaruEntity ashigaru && ashigaru.isRangeType() ? modelRangeResource : modelMeleeResource;
   }

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
      return animationResource;
   }
}
