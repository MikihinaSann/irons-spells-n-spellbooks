package net.neoforged.bus.api;

import java.util.function.Consumer;

/**
 * Minimal event bus contract mirroring the NeoForge BusAPI surface used by this codebase.
 */
public interface IEventBus {
    void register(Object target);

    void unregister(Object object);

    <T extends Event> void addListener(Consumer<T> consumer);

    <T extends Event> void addListener(Class<T> eventType, Consumer<T> consumer);

    <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer);

    <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer);

    <T extends Event> void addListener(boolean receiveCanceled, Consumer<T> consumer);

    <T extends Event> void addListener(boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer);

    <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer);

    <T extends Event> T post(T event);

    <T extends Event> T post(EventPriority phase, T event);
}
