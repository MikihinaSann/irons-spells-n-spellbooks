package be.florens.expandability.mixin.swimming;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.Util;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Player.class})
public abstract class PlayerMixin {
   @ModifyExpressionValue(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V", "tryToStartFallFlying()Z"},
      require = 2,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;isInWater()Z"
      )}
   )
   private boolean setInWater(boolean original) {
      Player self = (Player)(Object)this;
      return Util.processEventResult(EventDispatcher.onPlayerSwim(self), original);
   }

   @ModifyExpressionValue(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"},
      allow = 1,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"
      )}
   )
   private boolean cancelSurfaceCheck(boolean original) {
      Player self = (Player)(Object)this;
      return Util.processEventResult(EventDispatcher.onPlayerSwim(self), false, true, original);
   }
}
