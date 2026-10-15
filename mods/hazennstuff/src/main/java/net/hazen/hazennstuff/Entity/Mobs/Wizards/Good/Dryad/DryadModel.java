package net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.Dryad;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class DryadModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/dryad.png");
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/mobs/dryad.geo.json");
   }
}
