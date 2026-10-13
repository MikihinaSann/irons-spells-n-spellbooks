package net.neoforged.neoforge.data.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Datagen event stub — Fabric doesn't run data generation at runtime; kept for
 * compile compatibility with ISS's datagen wiring. {@link #getGenerator()}
 * returns a lightweight facade exposing the NeoForge-style API surface.
 */
public class GatherDataEvent extends Event {
    private final GeneratorFacade generator;
    private final boolean includeServer;
    private final boolean includeClient;
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public GatherDataEvent(DataGenerator generator, PackOutput output,
                           CompletableFuture<HolderLookup.Provider> lookupProvider,
                           boolean includeServer, boolean includeClient) {
        this.generator = new GeneratorFacade(generator, output);
        this.output = output;
        this.lookupProvider = lookupProvider;
        this.includeServer = includeServer;
        this.includeClient = includeClient;
    }

    /** NeoForge-style generator facade. */
    public GeneratorFacade getGenerator() {
        return generator;
    }

    public PackOutput getPackOutput() {
        return output;
    }

    public CompletableFuture<HolderLookup.Provider> getLookupProvider() {
        return lookupProvider;
    }

    public ExistingFileHelper getExistingFileHelper() {
        return new ExistingFileHelper();
    }

    public boolean includeServer() {
        return includeServer;
    }

    public boolean includeClient() {
        return includeClient;
    }

    /** Wraps the vanilla DataGenerator with NeoForge-style helpers. */
    public record GeneratorFacade(DataGenerator delegate, PackOutput output) {
        public PackOutput getPackOutput() {
            return output;
        }

        /** Datagen never runs on Fabric builds — providers are dropped. */
        public void addProvider(boolean run, DataProvider provider) {
        }

        public void addProvider(boolean run, java.util.function.Supplier<DataProvider> provider) {
        }

        public DataGenerator getVanillaGenerator() {
            return delegate;
        }
    }
}
