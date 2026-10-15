package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class VoidboundCodex3SpellBook extends AbstractAffinitySpellBook {
   public VoidboundCodex3SpellBook() {
      super(
         12,
         SchoolRegistry.ENDER_RESOURCE,
         "voidbound_codex",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ENDER,
         SpellbookProgressHelper.enderTheme(),
         new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
         mana(300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.ABYSSAL_ORBS.get(), 1);
      }
   }
}
