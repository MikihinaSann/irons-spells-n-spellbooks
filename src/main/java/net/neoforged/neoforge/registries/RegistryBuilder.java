package net.neoforged.neoforge.registries;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Stand-in for NeoForge's RegistryBuilder — backed by FabricRegistryBuilder.
 */
public class RegistryBuilder<T> {
    private final ResourceKey<Registry<T>> key;
    private boolean synced = false;
    private int maxId = -1;

    public RegistryBuilder(ResourceKey<Registry<T>> key) {
        this.key = key;
    }

    public RegistryBuilder<T> sync(boolean sync) {
        this.synced = sync;
        return this;
    }

    public RegistryBuilder<T> maxId(int maxId) {
        this.maxId = maxId;
        return this;
    }

    // No-op stubs matching NeoForge API
    public RegistryBuilder<T> disableSaving() {
        return this;
    }

    public RegistryBuilder<T> disableOverrides() {
        return this;
    }

    public RegistryBuilder<T> disableSync() {
        this.synced = false;
        return this;
    }

    public RegistryBuilder<T> add(HolderLookupCallback<T> callback) {
        return this;
    }

    public interface HolderLookupCallback<T> {
    }

    public Registry<T> create() {
        FabricRegistryBuilder<T, MappedRegistry<T>> builder = FabricRegistryBuilder.createSimple(key);
        if (synced) {
            builder.attribute(RegistryAttribute.SYNCED);
        }
        return builder.buildAndRegister();
    }
}
