package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

public class SoullitTestament2SpellBook extends AbstractAffinitySpellBook {
   public SoullitTestament2SpellBook() {
      super(
         10,
         MagicFromTheEastCompat.spiritSchoolOrEvocation(),
         "soullit_testament",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.UNDEAD,
         SpellbookProgressHelper.spiritTheme(),
         affinityAttributes(Optional.of(MagicFromTheEastCompat.getSpiritSpellPowerAttributeOrEvocation()), 0.15, 200)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         if (ModSpells.PROCESSION_OF_SOULS != null) {
            AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.PROCESSION_OF_SOULS.get(), 1);
         }
      }
   }
}
