package net.neoforged.neoforge.client.event;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.Event;

/** NeoForge RegisterClientReloadListenersEvent - drained into FAPI's CLIENT resource-manager listeners. */
public class RegisterClientReloadListenersEvent extends Event {
    private final java.util.List<ResourceManagerReloadListener> listeners = new java.util.ArrayList<>();

    public void registerReloadListener(ResourceManagerReloadListener listener) {
        listeners.add(listener);
    }

    public java.util.List<ResourceManagerReloadListener> getListeners() {
        return listeners;
    }
}
