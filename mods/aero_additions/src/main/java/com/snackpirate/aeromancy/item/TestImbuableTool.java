package com.snackpirate.aeromancy.item;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class TestImbuableTool extends Item implements IPresetSpellContainer {
   public TestImbuableTool(Properties properties) {
      super(properties);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack.getItem() instanceof TestImbuableTool && !ISpellContainer.isSpellContainer(itemStack)) {
         ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
         spellContainer.addSpell((AbstractSpell)SpellRegistry.SPECTRAL_HAMMER_SPELL.get(), 6, true);
         itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
      }
   }
}
