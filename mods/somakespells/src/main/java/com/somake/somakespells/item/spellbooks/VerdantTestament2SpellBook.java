package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class VerdantTestament2SpellBook extends AbstractAffinitySpellBook {
   public VerdantTestament2SpellBook() {
      super(
         10,
         SchoolRegistry.NATURE_RESOURCE,
         "verdant_testament",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.POISONED,
         SpellbookProgressHelper.natureTheme(),
         new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
