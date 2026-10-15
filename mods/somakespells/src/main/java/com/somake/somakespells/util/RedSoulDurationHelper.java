package com.somake.somakespells.util;

import com.somake.somakespells.mixin.MobEffectInstanceAccessor;
import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public final class RedSoulDurationHelper {
   private RedSoulDurationHelper() {
   }

   public static boolean addTime(ServerPlayer player, int ticks) {
      if (ModMobEffects.RED_SOUL == null) {
         return false;
      } else {
         MobEffectInstance effect = player.getEffect(ModMobEffects.RED_SOUL);
         if (effect != null && !effect.isInfiniteDuration() && ticks > 0) {
            int updated = Math.min(7200, effect.getDuration() + ticks);
            return replace(player, effect, updated);
         } else {
            return false;
         }
      }
   }

   public static boolean consumeTime(ServerPlayer player, int ticks, int minimumRemainingTicks) {
      if (ModMobEffects.RED_SOUL == null) {
         return false;
      } else {
         MobEffectInstance effect = player.getEffect(ModMobEffects.RED_SOUL);
         return effect != null && !effect.isInfiniteDuration() && ticks > 0 && effect.getDuration() - ticks >= minimumRemainingTicks
            ? replace(player, effect, effect.getDuration() - ticks)
            : false;
      }
   }

   private static boolean replace(ServerPlayer player, MobEffectInstance current, int duration) {
      if (duration == current.getDuration()) {
         return false;
      } else {
         ((MobEffectInstanceAccessor)current).somake$setDuration(duration);
         player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), current, false));
         return true;
      }
   }
}
