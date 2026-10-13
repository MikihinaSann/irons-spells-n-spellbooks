package net.neoforged.neoforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

/**
 * NeoForge-style tag provider stub. Datagen output is committed in the repo, so
 * this never actually runs on Fabric — it only needs to compile. Not to be
 * confused with vanilla {@code net.minecraft.data.tags.TagsProvider}; subclasses
 * must reference this type explicitly.
 */
public abstract class TagsProvider<T> implements DataProvider {
    protected final PackOutput output;
    protected final ResourceKey<? extends Registry<T>> registryKey;
    protected final CompletableFuture<HolderLookup.Provider> lookupProvider;
    protected final String modId;

    protected TagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
                           CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        this.output = output;
        this.registryKey = registryKey;
        this.lookupProvider = lookupProvider;
        this.modId = modId;
    }

    protected TagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
                           CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this(output, registryKey, lookupProvider, "minecraft");
    }

    protected abstract void addTags(HolderLookup.Provider provider);

    protected TagAppender<T> tag(TagKey<T> key) {
        return new TagAppender<>();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Tags for " + registryKey.location();
    }

    /** No-op tag builder matching the vanilla {@code TagAppender} surface. */
    public static class TagAppender<T> {
        @SafeVarargs
        public final TagAppender<T> add(ResourceKey<T>... keys) {
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> addTags(TagKey<T>... keys) {
            return this;
        }

        public TagAppender<T> addTag(TagKey<T> key) {
            return this;
        }

        public TagAppender<T> addOptionalTag(ResourceLocation location) {
            return this;
        }

        public TagAppender<T> addOptionalTag(TagKey<T> key) {
            return this;
        }

        public TagAppender<T> addOptional(ResourceLocation location) {
            return this;
        }
    }
}
