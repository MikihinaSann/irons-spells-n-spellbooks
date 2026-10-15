package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Necromancers.NamelessOne;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class NamelessOneModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/nameless_one.png");
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "geo/skeleton_mob.geo.json");
   }
}
