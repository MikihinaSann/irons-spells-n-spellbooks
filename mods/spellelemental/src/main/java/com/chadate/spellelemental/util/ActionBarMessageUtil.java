package com.chadate.spellelemental.util;

import com.chadate.spellelemental.network.ActionBarMessagePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ActionBarMessageUtil {
   public static void sendActionBarMessage(ServerPlayer player, Component message) {
      PacketDistributor.sendToPlayer(player, new ActionBarMessagePacket(message), new CustomPacketPayload[0]);
   }

   public static void sendActionBarMessage(ServerPlayer player, String message) {
      sendActionBarMessage(player, Component.literal(message));
   }

   public static void sendTranslatableActionBarMessage(ServerPlayer player, String key, Object... args) {
      sendActionBarMessage(player, Component.translatable(key, args));
   }
}
