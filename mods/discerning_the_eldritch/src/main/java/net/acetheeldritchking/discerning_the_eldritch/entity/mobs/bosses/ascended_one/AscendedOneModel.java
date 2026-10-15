package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class AscendedOneModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/apothic_cultists/ascended_one.png");
   }

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/casting_animations.json");
   }
}
