package net.neoforged.neoforge.network.registration;

import java.util.ArrayList;
import java.util.List;

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
