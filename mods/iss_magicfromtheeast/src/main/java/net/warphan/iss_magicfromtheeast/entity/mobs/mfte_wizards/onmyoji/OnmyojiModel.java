package net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.onmyoji;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class OnmyojiModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation TEXTURE = new ResourceLocation("iss_magicfromtheeast", "textures/entity/onmyoji.png");

   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return TEXTURE;
   }
}
