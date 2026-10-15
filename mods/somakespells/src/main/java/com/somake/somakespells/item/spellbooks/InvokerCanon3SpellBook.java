package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class InvokerCanon3SpellBook extends AbstractAffinitySpellBook {
   public InvokerCanon3SpellBook() {
      super(
         12,
         SchoolRegistry.EVOCATION_RESOURCE,
         "invoker_canon",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ILLAGER,
         SpellbookProgressHelper.evocationTheme(),
         new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
         mana(300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)SpellRegistry.SLOW_SPELL.get(), 1);
      }
   }
}
