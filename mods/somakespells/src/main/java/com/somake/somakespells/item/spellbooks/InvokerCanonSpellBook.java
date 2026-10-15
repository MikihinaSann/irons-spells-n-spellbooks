package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class InvokerCanonSpellBook extends AbstractAffinitySpellBook {
   public InvokerCanonSpellBook() {
      super(
         8,
         SchoolRegistry.EVOCATION_RESOURCE,
         "invoker_canon",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ILLAGER,
         SpellbookProgressHelper.evocationTheme(),
         new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         mana(100)
      );
   }
}
