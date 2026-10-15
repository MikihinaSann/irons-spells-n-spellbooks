package net.hazen.hazennstuff.Spells.AbstractSpells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public abstract class AbstractNightsEdgeSpell extends AbstractSpell {
   public Component getLockedMessage() {
      return Component.translatable("ui.hazennstuff.weapon_resonation");
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean canBeCraftedBy(Player player) {
      Item[] validItems = new Item[]{(Item)HnSItemRegistry.NIGHTS_EDGE.get(), (Item)HnSItemRegistry.TRUE_NIGHTS_EDGE.get()};

      for (Item item : validItems) {
         if (ASUtils.isValidUnlockItemInInventory(item, player)) {
            return true;
         }
      }

      return false;
   }
}
