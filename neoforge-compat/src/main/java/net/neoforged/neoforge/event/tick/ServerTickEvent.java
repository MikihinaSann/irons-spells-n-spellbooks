package net.neoforged.neoforge.event.tick;

import net.minecraft.server.MinecraftServer;

public abstract class ServerTickEvent extends TickEvent {
    private final MinecraftServer server;
    private final boolean haveTime;

    public ServerTickEvent(boolean haveTime, MinecraftServer server) {
        this.server = server;
        this.haveTime = haveTime;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public boolean haveTime() {
        return haveTime;
    }

    public static class Pre extends ServerTickEvent {
        public Pre(boolean haveTime, MinecraftServer server) {
            super(haveTime, server);
        }
    }

    public static class Post extends ServerTickEvent {
        public Post(boolean haveTime, MinecraftServer server) {
            super(haveTime, server);
        }
    }
}
