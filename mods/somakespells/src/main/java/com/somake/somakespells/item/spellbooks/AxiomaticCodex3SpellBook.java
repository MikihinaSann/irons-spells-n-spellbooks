package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AxiomaticCodex3SpellBook extends AbstractAffinitySpellBook {
   public AxiomaticCodex3SpellBook() {
      super(
         12,
         ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "symmetry"),
         "axiomatic_codex",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.symmetryTheme(),
         affinityAttributes(MagicFromTheEastCompat.getSymmetrySpellPowerAttribute(), 0.25, 300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         if (ModSpells.MIRROR_STRIKE != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.MIRROR_STRIKE.get(), 1);
         }
      }
   }
}
