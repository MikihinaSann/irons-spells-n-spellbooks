package io.redspace.ironslib.internal.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PlatformPacketDistributor {
   public static void register() {
   }

   private PlatformPacketDistributor() {
   }

   public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToServer(payload, payloads);
   }

   public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToPlayer(player, payload, payloads);
   }

   public static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToAllPlayers(payload, payloads);
   }

   public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToPlayersTrackingEntity(entity, payload, payloads);
   }

   public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload, payloads);
   }

   public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload, payloads);
   }
}
