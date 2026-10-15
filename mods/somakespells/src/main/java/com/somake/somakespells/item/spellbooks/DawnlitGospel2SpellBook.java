package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DawnlitGospel2SpellBook extends AbstractAffinitySpellBook {
   public DawnlitGospel2SpellBook() {
      super(
         10,
         SchoolRegistry.HOLY_RESOURCE,
         "dawnlit_gospel",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.UNDEAD,
         SpellbookProgressHelper.holyTheme(),
         new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
