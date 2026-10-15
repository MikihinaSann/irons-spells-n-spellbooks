package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StormcallerLexicon2SpellBook extends AbstractAffinitySpellBook {
   public StormcallerLexicon2SpellBook() {
      super(
         10,
         SchoolRegistry.LIGHTNING_RESOURCE,
         "stormcaller_lexicon",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ELECTROCUTED,
         SpellbookProgressHelper.lightningTheme(),
         new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
