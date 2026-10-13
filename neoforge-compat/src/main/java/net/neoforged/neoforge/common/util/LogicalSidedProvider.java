package net.neoforged.neoforge.common.util;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.LogicalSide;

/** NeoForge's LogicalSidedProvider - side-resolved singleton access. */
public class LogicalSidedProvider<T> {
    public static final LogicalSidedProvider<MinecraftServer> WORKQUEUE = new LogicalSidedProvider<>();
    public static final LogicalSidedProvider<Minecraft> CLIENTWORLD = new LogicalSidedProvider<>();

    private LogicalSidedProvider() {
    }

    public static void setClient(Supplier<Minecraft> client) {
        CLIENTWORLD.client = client;
    }

    public static void setServer(Supplier<MinecraftServer> server) {
        WORKQUEUE.server = server;
    }

    private Supplier<Minecraft> client;
    private Supplier<MinecraftServer> server;

    @SuppressWarnings("unchecked")
    public T get(LogicalSide side) {
        if (this == WORKQUEUE) {
            if (server != null) {
                return (T) server.get();
            }
            return null;
        }
        if (this == CLIENTWORLD) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                return (T) (client != null ? client.get() : Minecraft.getInstance());
            }
            return null;
        }
        return null;
    }
}
