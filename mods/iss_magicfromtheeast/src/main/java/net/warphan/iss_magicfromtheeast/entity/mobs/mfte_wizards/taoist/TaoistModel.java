package net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.taoist;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class TaoistModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation TEXTURE = new ResourceLocation("iss_magicfromtheeast", "textures/entity/taoist.png");

   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return TEXTURE;
   }
}
