package net.acetheeldritchking.aces_spell_utils.items.curios;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ImbueableCurioItem extends SimpleDescriptiveCurio implements IPresetSpellContainer {
   public ImbueableCurioItem(Properties properties, String slotIdentifier) {
      super(properties, slotIdentifier);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (itemStack.getItem() instanceof CurioBaseItem curioBaseItem && !ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, false);
            ISpellContainer.set(itemStack, spellContainer);
         }
      }
   }
}
