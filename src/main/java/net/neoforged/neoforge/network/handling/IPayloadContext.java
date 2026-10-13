package net.neoforged.neoforge.network.handling;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Stand-in for NeoForge's IPayloadContext.
 */
public interface IPayloadContext {
    /**
     * Schedule work on the main thread of the receiving side.
     */
    void enqueueWork(Runnable runnable);

    /**
     * NeoForge's Supplier overload — runs inline on Fabric since payload
     * handling already happens on the correct thread.
     */
    default <T> java.util.concurrent.CompletableFuture<T> enqueueWork(java.util.function.Supplier<T> work) {
        return java.util.concurrent.CompletableFuture.completedFuture(work.get());
    }

    /**
     * The player for this context — the local player on the client, the sender on
     * the server.
     */
    @Nullable
    Player player();

    /**
     * The level for this context.
     */
    @Nullable
    Level level();

    /**
     * Sends a payload back to the sender.
     */
    default void reply(CustomPacketPayload payload) {
    }

    /**
     * Whether the remote side declared this payload type; always true on the shim
     * (vanilla channels are always registered).
     */
    default boolean isChannelVersionMismatch() {
        return false;
    }

    /**
     * Disconnect the sender (server-side handler only).
     */
    default void disconnect(net.minecraft.network.chat.Component reason) {
    }
}
