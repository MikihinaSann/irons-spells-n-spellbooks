package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class StarlessGrimoire3SpellBook extends AbstractAffinitySpellBook {
   public StarlessGrimoire3SpellBook() {
      super(
         12,
         SchoolRegistry.ELDRITCH_RESOURCE,
         "starless_grimoire",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.ENDER,
         SpellbookProgressHelper.eldritchTheme(),
         new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
         mana(300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.ELDRITCH_GAMBIT.get(), 1);
      }
   }
}
