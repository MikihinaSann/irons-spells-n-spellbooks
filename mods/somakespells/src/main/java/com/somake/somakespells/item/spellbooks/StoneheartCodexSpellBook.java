package com.somake.somakespells.item.spellbooks;

import net.minecraft.resources.ResourceLocation;

public class StoneheartCodexSpellBook extends AbstractOptionalAttributeSpellBook {
   public StoneheartCodexSpellBook() {
      super(
         8,
         ResourceLocation.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo"),
         "stoneheart_codex",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.geoTheme(),
         ResourceLocation.parse("gtbcs_geomancy_plus:geo_spell_power"),
         0.05,
         100
      );
   }
}
