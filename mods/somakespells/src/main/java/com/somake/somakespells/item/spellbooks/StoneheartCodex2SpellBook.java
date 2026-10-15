package com.somake.somakespells.item.spellbooks;

import net.minecraft.resources.ResourceLocation;

public class StoneheartCodex2SpellBook extends AbstractOptionalAttributeSpellBook {
   public StoneheartCodex2SpellBook() {
      super(
         10,
         ResourceLocation.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo"),
         "stoneheart_codex",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.geoTheme(),
         ResourceLocation.parse("gtbcs_geomancy_plus:geo_spell_power"),
         0.15,
         200
      );
   }
}
