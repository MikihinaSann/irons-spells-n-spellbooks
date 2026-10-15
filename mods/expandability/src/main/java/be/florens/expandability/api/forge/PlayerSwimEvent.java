package be.florens.expandability.api.forge;

import be.florens.expandability.api.EventResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerSwimEvent extends PlayerEvent {
   private EventResult result = EventResult.PASS;

   public PlayerSwimEvent(Player player) {
      super(player);
   }

   public EventResult getResult() {
      return this.result;
   }

   public void setResult(EventResult result) {
      this.result = result;
   }
}
