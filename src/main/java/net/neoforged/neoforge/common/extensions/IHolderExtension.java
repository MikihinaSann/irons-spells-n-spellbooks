package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * NeoForge-style {@link Holder} extension — injected onto
 * {@code net.minecraft.core.Holder} via Loom {@code injected_interfaces}.
 * Only declares methods missing from vanilla Holder.
 */
public interface IHolderExtension<T> {
    private Holder<T> self() {
        return (Holder<T>) this;
    }

    /**
     * The key of this holder's registry entry.
     *
     * @throws IllegalStateException if the holder is not a direct reference
     *                               holder (e.g. a tag-based holder)
     */
    default ResourceKey<T> getKey() {
        return self().unwrapKey().orElseThrow(() -> new IllegalStateException("Trying to access key on tag-based holder"));
    }
}
