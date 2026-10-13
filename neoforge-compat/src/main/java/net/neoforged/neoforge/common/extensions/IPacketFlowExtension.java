package net.neoforged.neoforge.common.extensions;

import net.minecraft.network.protocol.PacketFlow;

/**
 * NeoForge's PacketFlow extension — directional helpers patched onto PacketFlow.
 */
public interface IPacketFlowExtension {

    default boolean isClientbound() {
        return (Object) this == PacketFlow.CLIENTBOUND;
    }

    default boolean isServerbound() {
        return (Object) this == PacketFlow.SERVERBOUND;
    }
}
