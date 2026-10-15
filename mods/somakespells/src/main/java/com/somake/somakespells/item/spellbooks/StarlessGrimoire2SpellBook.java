package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StarlessGrimoire2SpellBook extends AbstractAffinitySpellBook {
   public StarlessGrimoire2SpellBook() {
      super(
         10,
         SchoolRegistry.ELDRITCH_RESOURCE,
         "starless_grimoire",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ENDER,
         SpellbookProgressHelper.eldritchTheme(),
         new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
