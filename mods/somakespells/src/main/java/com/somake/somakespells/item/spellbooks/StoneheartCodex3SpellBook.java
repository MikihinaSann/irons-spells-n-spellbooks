package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class StoneheartCodex3SpellBook extends AbstractOptionalAttributeSpellBook {
   public StoneheartCodex3SpellBook() {
      super(
         12,
         ResourceLocation.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo"),
         "stoneheart_codex",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.geoTheme(),
         ResourceLocation.parse("gtbcs_geomancy_plus:geo_spell_power"),
         0.25,
         300
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.EARTHBOUND.get(), 1);
      }
   }
}
