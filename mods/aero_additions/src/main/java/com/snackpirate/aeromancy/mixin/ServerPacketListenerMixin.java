package com.snackpirate.aeromancy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.snackpirate.aeromancy.spells.AASpells;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ServerGamePacketListenerImpl.class})
public abstract class ServerPacketListenerMixin {
   @Shadow
   public ServerPlayer player;

   @ModifyExpressionValue(
      method = {"tick"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientIsFloating:Z",
         opcode = 180
      )}
   )
   private boolean aero_additions$airstepAntiDisconnect(boolean floating) {
      return floating && this.player.hasEffect(AASpells.MobEffects.AIRSTEPPING) ? false : floating;
   }
}
