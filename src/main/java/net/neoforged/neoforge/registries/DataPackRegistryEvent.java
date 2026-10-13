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
        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
            DynamicRegistries.registerSynced(registryKey, codec);
        }

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
            DynamicRegistries.registerSynced(registryKey, codec, networkCodec != null ? networkCodec : codec);
        }
    }
}
