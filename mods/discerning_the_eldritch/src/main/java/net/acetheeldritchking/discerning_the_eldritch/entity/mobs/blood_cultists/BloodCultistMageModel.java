package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class BloodCultistMageModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/blood_cultists/blood_mage.png");
   }

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/casting_animations.json");
   }
}
