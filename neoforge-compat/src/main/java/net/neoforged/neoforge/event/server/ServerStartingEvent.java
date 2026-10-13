package net.neoforged.neoforge.event.server;

import net.minecraft.server.MinecraftServer;

/**
 * Fired when the server is starting up (before world load).
 */
public class ServerStartingEvent extends ServerLifecycleEvent {
    public ServerStartingEvent(MinecraftServer server) {
        super(server);
    }
}
