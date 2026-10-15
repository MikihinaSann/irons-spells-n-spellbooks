package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class ApothicSummonerModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/apothic_cultists/apothic_summoner.png");
   }
}
