package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.TunesCompat;

public class SonicBeats2SpellBook extends AbstractOptionalAttributeSpellBook {
   public SonicBeats2SpellBook() {
      super(
         10,
         TunesCompat.MELODY_SCHOOL_ID,
         "resonance_canticle",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.soundTheme(),
         TunesCompat.MELODY_SPELL_POWER_ID,
         0.15,
         200
      );
   }
}
