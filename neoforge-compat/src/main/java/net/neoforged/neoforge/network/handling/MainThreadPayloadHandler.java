package net.neoforged.neoforge.network.handling;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Stand-in for NeoForge's MainThreadPayloadHandler.
 */
@FunctionalInterface
public interface MainThreadPayloadHandler<T extends CustomPacketPayload> {
    void handle(T payload, IPayloadContext context);
}
