package com.legacy.dash.network;

import com.legacy.dash.network.c_to_s.DashVisualsPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class PacketHandler implements IModBusEvent {
   @SubscribeEvent
   public static void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1.2.0");
      registrar.playToServer(DashVisualsPayload.TYPE, DashVisualsPayload.STREAM_CODEC, DashVisualsPayload::handler);
   }

   public static void sendToClient(CustomPacketPayload packet, ServerPlayer serverPlayer) {
      if (!(serverPlayer instanceof FakePlayer)) {
         serverPlayer.connection.send(packet);
      }
   }

   public static void sendToClients(CustomPacketPayload packet, ServerLevel level) {
      level.players().forEach(player -> sendToClient(packet, player));
   }

   public static void sendToClients(CustomPacketPayload packet, ServerLevel level, BlockPos pos) {
      sendToClients(packet, level, pos, 256);
   }

   public static void sendToClients(CustomPacketPayload packet, ServerLevel level, BlockPos pos, int range) {
      Vec3 centerPos = Vec3.atCenterOf(pos);
      level.players().forEach(player -> {
         if (centerPos.distanceTo(player.position()) <= range) {
            sendToClient(packet, player);
         }
      });
   }

   public static void sendToClients(CustomPacketPayload packet) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         server.getPlayerList().getPlayers().forEach(player -> sendToClient(packet, player));
      }
   }

   public static void sendToServer(CustomPacketPayload packet) {
      Minecraft.getInstance().getConnection().send(packet);
   }
}
