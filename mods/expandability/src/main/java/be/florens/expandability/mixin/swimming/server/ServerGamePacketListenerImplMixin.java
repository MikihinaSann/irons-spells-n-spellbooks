package be.florens.expandability.mixin.swimming.server;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.api.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerGamePacketListenerImpl.class})
public abstract class ServerGamePacketListenerImplMixin {
   @Shadow
   private boolean clientIsFloating;
   @Shadow
   public ServerPlayer player;

   @Inject(
      method = {"handleMovePlayer(Lnet/minecraft/network/protocol/game/ServerboundMovePlayerPacket;)V"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientIsFloating:Z",
         shift = Shift.AFTER,
         opcode = 181
      )}
   )
   private void allowSwimFlying(CallbackInfo info) {
      if (EventDispatcher.onPlayerSwim(this.player) == EventResult.SUCCESS) {
         this.clientIsFloating = false;
      }
   }
}
