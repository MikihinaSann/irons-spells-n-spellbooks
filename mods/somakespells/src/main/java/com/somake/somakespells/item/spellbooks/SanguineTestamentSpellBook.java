package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SanguineTestamentSpellBook extends AbstractAffinitySpellBook {
   public SanguineTestamentSpellBook() {
      super(
         8,
         SchoolRegistry.BLOOD_RESOURCE,
         "sanguine_testament",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ANY,
         SpellbookProgressHelper.bloodTheme(),
         new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         mana(100)
      );
   }
}
