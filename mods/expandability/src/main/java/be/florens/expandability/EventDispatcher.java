package be.florens.expandability;

import be.florens.expandability.api.EventResult;
import be.florens.expandability.neoforge.EventDispatcherImpl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.ApiStatus.Internal;

public class EventDispatcher {
   @Internal
   public static EventResult onPlayerSwim(Player player) {
      return EventDispatcherImpl.onPlayerSwim(player);
   }

   @Internal
   public static boolean onLivingFluidCollision(LivingEntity entity, FluidState fluidState) {
      return EventDispatcherImpl.onLivingFluidCollision(entity, fluidState);
   }
}
