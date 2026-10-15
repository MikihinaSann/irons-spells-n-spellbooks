package net.alshanex.alshanex_familiars.item;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.alshanex.familiarslib.item.AbstractFamiliarTotem;
import net.minecraft.world.item.Rarity;

public class TinyTotem extends AbstractFamiliarTotem {
   public TinyTotem() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.UNCOMMON));
   }
}
