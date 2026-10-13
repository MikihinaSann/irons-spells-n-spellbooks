package net.neoforged.neoforge.registries;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge's DataPackRegistriesHooks - backed by the DataPackRegistryEvent data
 * collected by the Fabric lifecycle driver.
 */
public final class DataPackRegistriesHooks {

    private DataPackRegistriesHooks() {
    }

    private static List<RegistryData> allRegistries = List.of();
    private static final java.util.Map<ResourceKey<? extends Registry<?>>, RegistryData> synced = new java.util.HashMap<>();

    /** NeoForge-internal record describing a datapack registry. */
    public record RegistryData(ResourceKey<? extends Registry<?>> key, Codec<?> codec, Codec<?> networkCodec) {
        public boolean syncable() {
            return networkCodec != null;
        }
    }

    static void init(List<RegistryData> registries, java.util.Map<ResourceKey<? extends Registry<?>>, RegistryData> syncedMap) {
        allRegistries = registries;
        synced.clear();
        synced.putAll(syncedMap);
    }

    public static List<RegistryData> getDataPackRegistries() {
        return allRegistries;
    }

    @Nullable
    public static RegistryData getSyncedRegistry(ResourceKey<? extends Registry<?>> key) {
        return synced.get(key);
    }
}
