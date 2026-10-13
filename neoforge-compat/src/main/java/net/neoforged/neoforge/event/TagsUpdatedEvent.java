package net.neoforged.neoforge.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.neoforged.bus.api.Event;

/** NeoForge TagsUpdatedEvent - fired after tags (re)load; backed by FAPI's CommonLifecycleEvents.TAGS_LOADED. */
public class TagsUpdatedEvent extends Event {

    public enum UpdateCause {
        CLIENT_PACKET_RECEIVED, SERVER_DATA_LOAD;
    }

    private final HolderLookup.Provider registryAccess;
    private final boolean integratedServer;
    private final UpdateCause updateCause;

    public TagsUpdatedEvent(HolderLookup.Provider registryAccess, boolean integratedServer, UpdateCause updateCause) {
        this.registryAccess = registryAccess;
        this.integratedServer = integratedServer;
        this.updateCause = updateCause;
    }

    public HolderLookup.Provider getRegistryAccess() {
        return registryAccess;
    }

    public boolean isFromIntegratedServer() {
        return integratedServer;
    }

    public UpdateCause getUpdateCause() {
        return updateCause;
    }
}
