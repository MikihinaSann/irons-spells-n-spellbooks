package net.acetheeldritchking.aces_spell_utils.items.weapons.maces;

import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class PresetImbueMaceItem extends ExtendedMaceItem implements IPresetSpellContainer {
   List<SpellData> spellData = null;
   SpellDataRegistryHolder[] spellDataRegistryHolders;

   public PresetImbueMaceItem(Tier pTier, Properties properties, SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(properties);
      this.spellDataRegistryHolders = spellDataRegistryHolders;
   }

   public List<SpellData> getSpells() {
      if (this.spellData == null) {
         this.spellData = Arrays.stream(this.spellDataRegistryHolders).<SpellData>map(SpellDataRegistryHolder::getSpellData).toList();
         this.spellDataRegistryHolders = null;
      }

      return this.spellData;
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null && !ISpellContainer.isSpellContainer(itemStack)) {
         List<SpellData> spells = this.getSpells();
         ISpellContainerMutable spellContainer = ISpellContainer.create(spells.size(), true, false).mutableCopy();
         spells.forEach(spellData -> spellContainer.addSpell(spellData.getSpell(), spellData.getLevel(), true));
         itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
      }
   }
}
