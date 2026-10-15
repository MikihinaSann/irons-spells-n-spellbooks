package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.TunesCompat;

public class SonicBeatsSpellBook extends AbstractOptionalAttributeSpellBook {
   public SonicBeatsSpellBook() {
      super(
         8,
         TunesCompat.MELODY_SCHOOL_ID,
         "resonance_canticle",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.soundTheme(),
         TunesCompat.MELODY_SPELL_POWER_ID,
         0.05,
         100
      );
   }
}
