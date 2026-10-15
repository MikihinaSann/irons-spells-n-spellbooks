package net.neoforged.neoforge.registries;

import java.util.function.Consumer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

/**
 * NeoForge RegisterEvent - fired once per registry while deferred entries are
 * written. Listeners inspect {@link #getRegistryKey()} and either call
 * {@link #register(ResourceKey, Consumer)} or work on {@link #getRegistry()}.
 */
public class RegisterEvent extends Event {
    private final Registry<?> registry;

    public RegisterEvent(Registry<?> registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public <T> Registry<T> getRegistry() {
        return (Registry<T>) registry;
    }

    public ResourceKey<? extends Registry<?>> getRegistryKey() {
        return registry.key();
    }

    public <T> void register(ResourceKey<? extends Registry<T>> key, Consumer<RegisterHelper<T>> consumer) {
        if (key.equals(this.registry.key())) {
            consumer.accept(new RegisterHelper<>(getRegistry()));
        }
    }

    /** NeoForge-style single-value registration: registers when this event's registry matches. */
    @SuppressWarnings("unchecked")
    public <T> void register(ResourceKey<? extends Registry<T>> key, ResourceLocation name, java.util.function.Supplier<? extends T> value) {
        if (key.equals(this.registry.key())) {
            Registry.register((Registry<T>) registry, name, value.get());
        }
    }

    public static class RegisterHelper<T> {
        private final Registry<T> registry;

        public RegisterHelper(Registry<T> registry) {
            this.registry = registry;
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        public void register(ResourceLocation name, T value) {
            Registry.register(registry, name, value);
        }

        public void register(String name, T value) {
            register(ResourceLocation.parse(name), value);
        }
    }
}
