package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BurningMagma2SpellBook extends AbstractAffinitySpellBook {
   public BurningMagma2SpellBook() {
      super(
         10,
         SchoolRegistry.FIRE_RESOURCE,
         "burning_magma",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.BURNING,
         SpellbookProgressHelper.fireTheme(),
         new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
