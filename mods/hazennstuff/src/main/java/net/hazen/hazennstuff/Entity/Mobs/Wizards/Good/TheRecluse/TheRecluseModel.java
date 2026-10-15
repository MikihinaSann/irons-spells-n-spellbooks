package net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.TheRecluse;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class TheRecluseModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/enderman_spellcasting_mob.png");
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath(
      "hazennstuff", "geo/entities/mobs/enderman_spellcasting_mob.geo.json"
   );
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/casting_animation.json");

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
