package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

public class SoullitTestament3SpellBook extends AbstractAffinitySpellBook {
   public SoullitTestament3SpellBook() {
      super(
         12,
         MagicFromTheEastCompat.spiritSchoolOrEvocation(),
         "soullit_testament",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.UNDEAD,
         SpellbookProgressHelper.spiritTheme(),
         affinityAttributes(Optional.of(MagicFromTheEastCompat.getSpiritSpellPowerAttributeOrEvocation()), 0.25, 300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         if (ModSpells.SOUL_GRAB != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SOUL_GRAB.get(), 1);
         }

         if (ModSpells.GRAVE_SIGIL != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.GRAVE_SIGIL.get(), 1);
         }

         if (ModSpells.SOUL_BASTION != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SOUL_BASTION.get(), 1);
         }

         if (ModSpells.SOUL_LATCH != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SOUL_LATCH.get(), 1);
         }

         if (ModSpells.SPIRAL_OF_RUIN != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SPIRAL_OF_RUIN.get(), 1);
         }

         if (ModSpells.SOULFALL_JUDGMENT != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SOULFALL_JUDGMENT.get(), 1);
         }

         if (ModSpells.SOUL_REPRISAL != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SOUL_REPRISAL.get(), 1);
         }

         if (ModSpells.SPECTRAL_RONDO != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.SPECTRAL_RONDO.get(), 1);
         }

         if (ModSpells.WINGED_RUIN != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.WINGED_RUIN.get(), 1);
         }
      }
   }
}
