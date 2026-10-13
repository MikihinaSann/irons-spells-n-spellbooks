package net.neoforged.neoforge.client.event;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Chat message received on the client. The unbounded variant plus
 * {@code Player}/{@code System} specializations exist in NeoForge; this shim
 * provides the generic one.
 */
public class ClientChatReceivedEvent extends Event implements ICancellableEvent {
    private Component message;
    private final boolean system;

    public ClientChatReceivedEvent(Component message, boolean system) {
        this.message = message;
        this.system = system;
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public boolean isSystem() {
        return system;
    }

    /** A player-authored chat message. */
    public static class Player extends ClientChatReceivedEvent {
        private final java.util.UUID sender;
        private final net.minecraft.network.chat.Component boundChatMessage;

        public Player(Component message, java.util.UUID sender, Component boundChatMessage) {
            super(message, false);
            this.sender = sender;
            this.boundChatMessage = boundChatMessage;
        }

        public java.util.UUID getSender() {
            return sender;
        }

        public Component getBoundChatMessage() {
            return boundChatMessage;
        }
    }

    /** A system (server) message. */
    public static class System extends ClientChatReceivedEvent {
        public System(Component message) {
            super(message, true);
        }
    }
}
