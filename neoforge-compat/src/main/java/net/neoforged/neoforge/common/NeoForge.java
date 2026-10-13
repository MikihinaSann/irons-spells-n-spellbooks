package net.neoforged.neoforge.common;

import net.neoforged.neoforge.internal.fabric.FabricEventBus;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.server.MinecraftServer;

/**
 * NeoForge common class stand-in. {@link #EVENT_BUS} is a real (if minimal) event bus
 * that the Fabric bridges post into.
 */
public class NeoForge {
    public static final IEventBus EVENT_BUS = new FabricEventBus();

    private NeoForge() {
    }

    public static MinecraftServer getServer() {
        return net.neoforged.neoforge.internal.fabric.NeoForgeCompat.SERVER;
    }
}
