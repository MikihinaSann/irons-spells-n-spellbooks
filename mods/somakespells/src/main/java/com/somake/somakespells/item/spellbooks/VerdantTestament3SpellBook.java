package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class VerdantTestament3SpellBook extends AbstractAffinitySpellBook {
   public VerdantTestament3SpellBook() {
      super(
         12,
         SchoolRegistry.NATURE_RESOURCE,
         "verdant_testament",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.POISONED,
         SpellbookProgressHelper.natureTheme(),
         new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
         mana(300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)SpellRegistry.POISON_SPLASH_SPELL.get(), 1);
      }
   }
}
