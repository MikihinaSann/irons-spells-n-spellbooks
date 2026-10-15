package net.acetheeldritchking.aces_spell_utils.items.weapons.maces;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ImbueableMaceStaffItem extends MaceStaffItem implements IPresetSpellContainer {
   public ImbueableMaceStaffItem(Properties properties) {
      super(properties);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.getItem() instanceof MaceStaffItem maceItem && !ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, false);
            ISpellContainer.set(itemStack, spellContainer);
         }
      }
   }
}
