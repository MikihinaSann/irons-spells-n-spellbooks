package net.neoforged.neoforge.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.function.Supplier;

/**
 * NeoForge-style packet distribution implemented over Fabric networking.
 */
public class PacketDistributor {

    public static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... others) {
        sendToAllPlayers(payload);
        for (CustomPacketPayload other : others) {
            sendToAllPlayers(other);
        }
    }

    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... others) {
        sendToPlayersTrackingEntity(entity, payload);
        for (CustomPacketPayload other : others) {
            sendToPlayersTrackingEntity(entity, other);
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... others) {
        sendToPlayersTrackingEntityAndSelf(entity, payload);
        for (CustomPacketPayload other : others) {
            sendToPlayersTrackingEntityAndSelf(entity, other);
        }
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload, CustomPacketPayload... others) {
        sendToPlayersTrackingChunk(level, chunkPos, payload);
        for (CustomPacketPayload other : others) {
            sendToPlayersTrackingChunk(level, chunkPos, other);
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... others) {
        sendToPlayer(player, payload);
        for (CustomPacketPayload other : others) {
            sendToPlayer(player, other);
        }
    }

    public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... others) {
        if (FMLLoader.getDist() != Dist.CLIENT) {
            throw new IllegalStateException("Cannot send clientbound packet to server from a dedicated server");
        }
        ClientPlayNetworking.send(payload);
        for (CustomPacketPayload p : others) {
            ClientPlayNetworking.send(p);
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload) {
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(level, chunkPos)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload) {
        sendToPlayersTrackingEntity(entity, payload);
        if (entity instanceof ServerPlayer player) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        // Resolve the server from a tracked player or the singleton.
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    // ---- Target-chain API (compat stubs; ISS only uses these in dead code) ----

    public static final TargetPoint PLAYER = new TargetPoint(Target.PLAYER);
    public static final TargetPoint ALL = new TargetPoint(Target.ALL);
    public static final TargetPoint TRACKING_ENTITY = new TargetPoint(Target.TRACKING_ENTITY);
    public static final TargetPoint TRACKING_ENTITY_AND_SELF = new TargetPoint(Target.TRACKING_ENTITY_AND_SELF);
    public static final TargetPoint TRACKING_CHUNK = new TargetPoint(Target.TRACKING_CHUNK);
    public static final TargetPoint DIMENSION = new TargetPoint(Target.DIMENSION);

    public enum Target {
        PLAYER, ALL, TRACKING_ENTITY, TRACKING_ENTITY_AND_SELF, TRACKING_CHUNK, DIMENSION
    }

    public static class TargetPoint {
        private final Target target;
        private Supplier<?> arg;

        TargetPoint(Target target) {
            this.target = target;
        }

        public TargetPoint with(Supplier<?> arg) {
            this.arg = arg;
            return this;
        }

        public TargetPoint noArg() {
            return this;
        }

        public Target target() {
            return target;
        }

        public Supplier<?> arg() {
            return arg;
        }
    }
}
