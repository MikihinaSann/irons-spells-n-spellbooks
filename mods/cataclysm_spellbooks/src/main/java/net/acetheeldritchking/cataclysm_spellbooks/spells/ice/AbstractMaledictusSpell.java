package net.acetheeldritchking.cataclysm_spellbooks.spells.ice;

import com.github.L_Ender.cataclysm.init.ModItems;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public abstract class AbstractMaledictusSpell extends AbstractSpell {
   public boolean allowLooting() {
      return false;
   }

   public boolean canBeCraftedBy(Player player) {
      Item cursium = (Item)ModItems.CURSIUM_INGOT.get();
      return ASUtils.isValidUnlockItemInInventory(cursium, player);
   }
}
