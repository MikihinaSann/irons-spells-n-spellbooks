package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import java.util.Optional;

public class SoullitTestamentSpellBook extends AbstractAffinitySpellBook {
   public SoullitTestamentSpellBook() {
      super(
         8,
         MagicFromTheEastCompat.spiritSchoolOrEvocation(),
         "soullit_testament",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.UNDEAD,
         SpellbookProgressHelper.spiritTheme(),
         affinityAttributes(Optional.of(MagicFromTheEastCompat.getSpiritSpellPowerAttributeOrEvocation()), 0.05, 100)
      );
   }
}
