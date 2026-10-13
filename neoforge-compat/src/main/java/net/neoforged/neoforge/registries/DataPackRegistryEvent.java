package net.neoforged.neoforge.registries;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;

/**
 * Stand-in for NeoForge's datapack-registry event — delegated to Fabric's
 * {@link DynamicRegistries}.
 */
public class DataPackRegistryEvent extends Event {

    public static class NewRegistry extends DataPackRegistryEvent {
        private final java.util.List<DataPackRegistriesHooks.RegistryData> registries = new java.util.ArrayList<>();
        private final java.util.Map<ResourceKey<? extends Registry<?>>, DataPackRegistriesHooks.RegistryData> syncedRegistries = new java.util.HashMap<>();

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
            DynamicRegistries.registerSynced(registryKey, codec);
            var data = new DataPackRegistriesHooks.RegistryData(registryKey, codec, null);
            registries.add(data);
        }

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
            DynamicRegistries.registerSynced(registryKey, codec, networkCodec != null ? networkCodec : codec);
            var data = new DataPackRegistriesHooks.RegistryData(registryKey, codec, networkCodec);
            registries.add(data);
            if (networkCodec != null) {
                syncedRegistries.put(registryKey, data);
            }
        }

        /** Called by the lifecycle driver once the event has been fully posted. */
        public void finish() {
            java.util.List<DataPackRegistriesHooks.RegistryData> all = new java.util.ArrayList<>(DataPackRegistriesHooks.getDataPackRegistries());
            registries.forEach(r -> { if (!all.contains(r)) all.add(r); });
            DataPackRegistriesHooks.init(java.util.Collections.unmodifiableList(all), syncedRegistries);
        }
    }
}
