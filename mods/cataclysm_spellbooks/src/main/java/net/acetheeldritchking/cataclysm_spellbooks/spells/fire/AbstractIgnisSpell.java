package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.init.ModItems;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public abstract class AbstractIgnisSpell extends AbstractSpell {
   public boolean allowLooting() {
      return false;
   }

   public boolean canBeCraftedBy(Player player) {
      Item burningEmbers = (Item)ModItems.BURNING_ASHES.get();
      return ASUtils.isValidUnlockItemInInventory(burningEmbers, player);
   }
}
