package net.acetheeldritchking.aces_spell_utils.items.curios;

import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class PresetImbueCurio extends SimpleDescriptiveCurio implements IPresetSpellContainer {
   List<SpellData> spellData = null;
   SpellDataRegistryHolder[] spellDataRegistryHolders;

   public PresetImbueCurio(Properties properties, String slotIdentifier, SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(properties, slotIdentifier);
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
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, true).mutableCopy();
            this.getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), true));
            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
         }
      }
   }
}
