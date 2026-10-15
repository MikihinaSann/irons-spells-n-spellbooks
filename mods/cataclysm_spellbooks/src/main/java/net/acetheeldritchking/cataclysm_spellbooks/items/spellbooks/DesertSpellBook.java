package net.acetheeldritchking.cataclysm_spellbooks.items.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.acetheeldritchking.cataclysm_spellbooks.registries.SpellRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DesertSpellBook extends UniqueSpellBook {
   public DesertSpellBook() {
      super(
         SpellDataRegistryHolder.of(
            new SpellDataRegistryHolder[]{
               new SpellDataRegistryHolder(SpellRegistries.MONOLITH_CRASH, 5),
               new SpellDataRegistryHolder(SpellRegistries.DESERT_WINDS, 3),
               new SpellDataRegistryHolder(SpellRegistries.SANDSTORM, 3),
               new SpellDataRegistryHolder(SpellRegistries.THOTHS_WITNESS, 1),
               new SpellDataRegistryHolder(SpellRegistries.PHARAOHS_WRATH, 3)
            }
         ),
         7
      );
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
            new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.3F, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.2F, Operation.ADD_MULTIPLIED_BASE)
         }
      );
   }
}
