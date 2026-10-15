package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class FrostveinCodexSpellBook extends AbstractAffinitySpellBook {
   public FrostveinCodexSpellBook() {
      super(
         8,
         SchoolRegistry.ICE_RESOURCE,
         "frostvein_codex",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.FROZEN,
         SpellbookProgressHelper.iceTheme(),
         new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         mana(100)
      );
   }
}
