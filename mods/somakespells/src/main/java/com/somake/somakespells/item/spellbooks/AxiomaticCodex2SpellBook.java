package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import net.minecraft.resources.ResourceLocation;

public class AxiomaticCodex2SpellBook extends AbstractAffinitySpellBook {
   public AxiomaticCodex2SpellBook() {
      super(
         10,
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "symmetry"),
         "axiomatic_codex",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.symmetryTheme(),
         affinityAttributes(MagicFromTheEastCompat.getSymmetrySpellPowerAttribute(), 0.15, 200)
      );
   }
}
