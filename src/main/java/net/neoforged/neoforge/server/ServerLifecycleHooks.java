package net.neoforged.neoforge.server;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

/**
 * Tracks the running server instance (set by the platform bootstrap).
 */
public class ServerLifecycleHooks {
    private static volatile MinecraftServer currentServer;

    public static void setCurrentServer(@Nullable MinecraftServer server) {
        currentServer = server;
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }
}
