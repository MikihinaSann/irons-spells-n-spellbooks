package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DawnlitGospelSpellBook extends AbstractAffinitySpellBook {
   public DawnlitGospelSpellBook() {
      super(
         8,
         SchoolRegistry.HOLY_RESOURCE,
         "dawnlit_gospel",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.UNDEAD,
         SpellbookProgressHelper.holyTheme(),
         new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         mana(100)
      );
   }
}
