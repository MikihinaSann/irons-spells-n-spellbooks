package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import net.minecraft.resources.ResourceLocation;

public class AxiomaticCodexSpellBook extends AbstractAffinitySpellBook {
   public AxiomaticCodexSpellBook() {
      super(
         8,
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "symmetry"),
         "axiomatic_codex",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.symmetryTheme(),
         affinityAttributes(MagicFromTheEastCompat.getSymmetrySpellPowerAttribute(), 0.05, 100)
      );
   }
}
