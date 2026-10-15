package net.alshanex.alshanex_familiars.item;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.alshanex.familiarslib.util.iron_spells.StaffTier;
import net.minecraft.world.item.Rarity;

public class BardHarp extends StaffItem {
   public BardHarp() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(StaffTier.SOUND_STAFF)));
   }
}
