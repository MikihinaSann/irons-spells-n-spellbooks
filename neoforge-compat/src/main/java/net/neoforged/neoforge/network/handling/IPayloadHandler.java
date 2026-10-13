package net.neoforged.neoforge.network.handling;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** NeoForge-package alias of our registration.IPayloadHandler (same signature). */
@FunctionalInterface
public interface IPayloadHandler<T extends CustomPacketPayload>
        extends net.neoforged.neoforge.network.registration.IPayloadHandler<T> {
}
