package net.neoforged.bus.api;

/**
 * Marker interface matching NeoForge's ICancellableEvent — implemented by
 * events that can be cancelled. {@link Event} supplies the implementation.
 */
public interface ICancellableEvent {
    void setCanceled(boolean canceled);

    boolean isCanceled();
}
