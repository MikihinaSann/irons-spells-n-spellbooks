package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.TunesCompat;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.world.item.ItemStack;

public class SonicBeats3SpellBook extends AbstractOptionalAttributeSpellBook {
   public SonicBeats3SpellBook() {
      super(
         12,
         TunesCompat.MELODY_SCHOOL_ID,
         "resonance_canticle",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.soundTheme(),
         TunesCompat.MELODY_SPELL_POWER_ID,
         0.25,
         300
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, SpellRegistry.getSpell(TunesCompat.GRAND_FINALE_SPELL_ID.toString()), 1);
      }
   }
}
