package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.BishopOfDeciet;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class BishopOfDeceitModel extends AbstractSpellCastingMobModel {
   public ResourceLocation getTextureResource(AbstractSpellCastingMob mob) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/bishop_of_deceit.png");
   }
}
