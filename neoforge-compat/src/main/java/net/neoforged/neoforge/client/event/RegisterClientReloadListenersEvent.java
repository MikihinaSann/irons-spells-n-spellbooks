package net.neoforged.neoforge.client.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.Event;

/** NeoForge RegisterClientReloadListenersEvent - drained into FAPI's CLIENT resource-manager listeners. */
public class RegisterClientReloadListenersEvent extends Event {
    private final java.util.List<PreparableReloadListener> listeners = new java.util.ArrayList<>();

    public void registerReloadListener(PreparableReloadListener listener) {
        listeners.add(listener);
    }

    public java.util.List<PreparableReloadListener> getListeners() {
        return listeners;
    }
}
