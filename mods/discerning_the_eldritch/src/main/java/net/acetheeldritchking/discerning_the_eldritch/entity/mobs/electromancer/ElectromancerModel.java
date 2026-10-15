package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.electromancer;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class ElectromancerModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/electromancer/electromancer.png");
   }
}
