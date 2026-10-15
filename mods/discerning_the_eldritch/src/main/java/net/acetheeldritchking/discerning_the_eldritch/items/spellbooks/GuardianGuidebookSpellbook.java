package net.acetheeldritchking.discerning_the_eldritch.items.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class GuardianGuidebookSpellbook extends UniqueSpellBook {
   public GuardianGuidebookSpellbook() {
      super(SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistries.GUARDIANS_GAZE, 1)}), 9);
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE),
            new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.1F, Operation.ADD_MULTIPLIED_BASE)
         }
      );
   }
}
