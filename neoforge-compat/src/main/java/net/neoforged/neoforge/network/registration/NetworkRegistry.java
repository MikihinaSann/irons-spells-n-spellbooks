package net.neoforged.neoforge.network.registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Minimal NeoForge NetworkRegistry - static payload registration used by libs
 * (e.g. Placebo's PayloadHelper). Registrations queue up and drain through the
 * same path as {@link PayloadRegistrar.Registration}.
 */
public class NetworkRegistry {
    public static final List<Integer> SUPPORTED_COMMON_NETWORKING_VERSIONS = List.of(1);

    private static final List<PayloadRegistrar.Registration<?>> PENDING = new ArrayList<>();

    public static <T extends CustomPacketPayload> void register(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler,
            List<ConnectionProtocol> supportedProtocols,
            PacketFlow flow,
            String version,
            boolean optional) {
        PayloadRegistrar.Direction direction = switch (flow) {
            case CLIENTBOUND -> PayloadRegistrar.Direction.TO_CLIENT;
            case SERVERBOUND -> PayloadRegistrar.Direction.TO_SERVER;
        };
        PENDING.add(new PayloadRegistrar.Registration<>(type, codec, handler, direction, false));
    }

    /** NeoForge's overload with an optional flow — empty flow registers both directions. */
    public static <T extends CustomPacketPayload> void register(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler,
            List<ConnectionProtocol> supportedProtocols,
            Optional<PacketFlow> flow,
            String version,
            boolean optional) {
        PayloadRegistrar.Direction direction = flow.map(f -> switch (f) {
            case CLIENTBOUND -> PayloadRegistrar.Direction.TO_CLIENT;
            case SERVERBOUND -> PayloadRegistrar.Direction.TO_SERVER;
        }).orElse(PayloadRegistrar.Direction.BOTH);
        PENDING.add(new PayloadRegistrar.Registration<>(type, codec, handler, direction, false));
    }

    /**
     * Whether the connection has negotiated the channel. Fabric equivalent of
     * NeoForge's negotiated-channel tracking: delegates to the FAPI canSend checks
     * through the connection's packet listener.
     */
    public static boolean hasChannel(Connection connection, ConnectionProtocol protocol, net.minecraft.resources.ResourceLocation payloadId) {
        PacketListener listener = connection.getPacketListener();
        if (listener instanceof net.minecraft.server.network.ServerGamePacketListenerImpl serverListener) {
            return net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(serverListener, payloadId);
        }
        if (listener instanceof net.minecraft.client.multiplayer.ClientPacketListener) {
            return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(payloadId);
        }
        return false;
    }

    /** Convenience overload used by listeners without a protocol argument. */
    public static boolean hasChannel(Connection connection, net.minecraft.resources.ResourceLocation payloadId) {
        return hasChannel(connection, null, payloadId);
    }

    /** Called by the platform init after payload registrar draining. */
    public static void drain() {
        for (PayloadRegistrar.Registration<?> r : List.copyOf(PENDING)) {
            r.register();
        }
        PENDING.clear();
    }

    private NetworkRegistry() {
    }
}
