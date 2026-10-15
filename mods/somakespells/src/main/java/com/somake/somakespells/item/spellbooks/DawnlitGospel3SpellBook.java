package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class DawnlitGospel3SpellBook extends AbstractAffinitySpellBook {
   public DawnlitGospel3SpellBook() {
      super(
         12,
         SchoolRegistry.HOLY_RESOURCE,
         "dawnlit_gospel",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.UNDEAD,
         SpellbookProgressHelper.holyTheme(),
         new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
         mana(300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)SpellRegistry.HEALING_CIRCLE_SPELL.get(), 1);
      }
   }
}
