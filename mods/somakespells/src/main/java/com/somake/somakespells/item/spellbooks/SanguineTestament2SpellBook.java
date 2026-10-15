package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SanguineTestament2SpellBook extends AbstractAffinitySpellBook {
   public SanguineTestament2SpellBook() {
      super(
         10,
         SchoolRegistry.BLOOD_RESOURCE,
         "sanguine_testament",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.bloodTheme(),
         new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
