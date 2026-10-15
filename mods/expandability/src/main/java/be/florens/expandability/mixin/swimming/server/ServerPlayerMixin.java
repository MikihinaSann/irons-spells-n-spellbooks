package be.florens.expandability.mixin.swimming.server;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.Util;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ServerPlayer.class})
public abstract class ServerPlayerMixin {
   @ModifyExpressionValue(
      method = {"checkMovementStatistics(DDD)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayer;isInWater()Z"
      )}
   )
   private boolean setInWater(boolean original) {
      Player self = (Player)(Object)this;
      return Util.processEventResult(EventDispatcher.onPlayerSwim(self), original);
   }

   @ModifyExpressionValue(
      method = {"checkMovementStatistics(DDD)V"},
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerPlayer;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"
      )}
   )
   private boolean setEyeInFluid(boolean original) {
      Player self = (Player)(Object)this;
      return Util.processEventResult(EventDispatcher.onPlayerSwim(self), original);
   }
}
