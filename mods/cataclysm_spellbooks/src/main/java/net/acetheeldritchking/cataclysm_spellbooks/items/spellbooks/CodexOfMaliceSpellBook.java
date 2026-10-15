package net.acetheeldritchking.cataclysm_spellbooks.items.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CodexOfMaliceSpellBook extends SpellBook {
   public CodexOfMaliceSpellBook() {
      super(12, ItemPropertiesHelper.equipment().fireResistant().stacksTo(1));
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
            new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.3F, Operation.ADD_MULTIPLIED_BASE)
         }
      );
   }
}
