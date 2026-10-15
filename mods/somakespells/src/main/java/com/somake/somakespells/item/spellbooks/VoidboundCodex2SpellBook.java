package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class VoidboundCodex2SpellBook extends AbstractAffinitySpellBook {
   public VoidboundCodex2SpellBook() {
      super(
         10,
         SchoolRegistry.ENDER_RESOURCE,
         "voidbound_codex",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ENDER,
         SpellbookProgressHelper.enderTheme(),
         new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
