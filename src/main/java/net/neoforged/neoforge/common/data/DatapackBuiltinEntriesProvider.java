package net.neoforged.neoforge.common.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Datagen-only stub for NeoForge's DatapackBuiltinEntriesProvider.
 */
public abstract class DatapackBuiltinEntriesProvider implements DataProvider {
    protected final PackOutput output;
    protected final RegistrySetBuilder registrySetBuilder;
    private final CompletableFuture<net.minecraft.core.HolderLookup.Provider> registryProvider;

    protected DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<net.minecraft.core.HolderLookup.Provider> provider,
                                             RegistrySetBuilder registrySetBuilder, Set<String> modIds) {
        this.output = output;
        this.registrySetBuilder = registrySetBuilder;
        this.registryProvider = provider;
    }

    protected DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<net.minecraft.core.HolderLookup.Provider> provider,
                                             RegistrySetBuilder registrySetBuilder, String modId) {
        this(output, provider, registrySetBuilder, java.util.Set.of(modId));
    }

    public CompletableFuture<net.minecraft.core.HolderLookup.Provider> getRegistryProvider() {
        return registryProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Datapack builtin entries";
    }
}
