package net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.Dryad;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class DryadRenderer extends AbstractSpellCastingMobRenderer {
   public DryadRenderer(Context renderManager) {
      super(renderManager, new DryadModel());
   }
}
