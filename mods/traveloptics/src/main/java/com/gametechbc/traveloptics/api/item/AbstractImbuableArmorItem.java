package com.gametechbc.traveloptics.api.item;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public abstract class AbstractImbuableArmorItem extends GeoArmorItem implements IPresetSpellContainer {
   public AbstractImbuableArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributes) {
      super(material, type, properties, attributes);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.getItem() instanceof ArmorItem armorItem
            && this.getImbuableArmorTypes().contains(armorItem.getType())
            && !ISpellContainer.isSpellContainer(itemStack)) {
            int slots = this.getMaxSpellSlots().getOrDefault(armorItem.getType(), 0);
            ISpellContainerMutable spellContainer = ISpellContainer.create(slots, this.isAddToSpellWheel(), this.mustBeEquipped()).mutableCopy();
            Map<AbstractSpell, AbstractImbuableArmorItem.SpellData> spells = this.getSpellsToAdd().getOrDefault(armorItem.getType(), Collections.emptyMap());
            int index = 0;

            for (Entry<AbstractSpell, AbstractImbuableArmorItem.SpellData> entry : spells.entrySet()) {
               AbstractSpell spell = entry.getKey();
               AbstractImbuableArmorItem.SpellData data = entry.getValue();
               spellContainer.addSpellAtIndex(spell, data.level, index++, data.unlocked);
            }

            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
         }
      }
   }

   protected abstract Set<Type> getImbuableArmorTypes();

   protected abstract Map<Type, Integer> getMaxSpellSlots();

   protected Map<Type, Map<AbstractSpell, AbstractImbuableArmorItem.SpellData>> getSpellsToAdd() {
      return Collections.emptyMap();
   }

   protected boolean isAddToSpellWheel() {
      return true;
   }

   protected boolean mustBeEquipped() {
      return true;
   }

   public static class SpellData {
      public final int level;
      public final boolean unlocked;

      public SpellData(int level, boolean unlocked) {
         super();
         this.level = level;
         this.unlocked = unlocked;
      }
   }
}
