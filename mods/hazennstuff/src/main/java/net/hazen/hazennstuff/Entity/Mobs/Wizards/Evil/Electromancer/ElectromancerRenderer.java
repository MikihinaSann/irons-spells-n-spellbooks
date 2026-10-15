package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Electromancer;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class ElectromancerRenderer extends AbstractSpellCastingMobRenderer {
   public ElectromancerRenderer(Context context) {
      super(context, new ElectromancerModel());
   }
}
