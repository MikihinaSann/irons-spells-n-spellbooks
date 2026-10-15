package net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicTraitorModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class ApothicTraitorRenderer extends AbstractSpellCastingMobRenderer {
   public ApothicTraitorRenderer(Context renderManager) {
      super(renderManager, new ApothicTraitorModel());
   }
}
