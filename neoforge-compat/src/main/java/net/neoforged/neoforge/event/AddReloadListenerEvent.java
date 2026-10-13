package net.neoforged.neoforge.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects server resource reload listeners. Drained onto Fabric's
 * {@code ResourceManagerHelper} during init.
 */
public class AddReloadListenerEvent extends Event {
    private final List<PreparableReloadListener> listeners = new ArrayList<>();

    public void addListener(PreparableReloadListener listener) {
        listeners.add(listener);
    }

    public List<PreparableReloadListener> getListeners() {
        return listeners;
    }

    public ResourceManager getResourceManager() {
        return null;
    }
}
