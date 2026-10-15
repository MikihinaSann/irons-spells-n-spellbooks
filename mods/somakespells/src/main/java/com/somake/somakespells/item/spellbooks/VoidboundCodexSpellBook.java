package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class VoidboundCodexSpellBook extends AbstractAffinitySpellBook {
   public VoidboundCodexSpellBook() {
      super(
         8,
         SchoolRegistry.ENDER_RESOURCE,
         "voidbound_codex",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ENDER,
         SpellbookProgressHelper.enderTheme(),
         new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         mana(100)
      );
   }
}
