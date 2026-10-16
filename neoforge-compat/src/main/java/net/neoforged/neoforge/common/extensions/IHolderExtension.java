package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.IWithData;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge-style {@link Holder} extension — injected onto
 * {@code net.minecraft.core.Holder} via Loom {@code injected_interfaces}.
 * Only declares methods missing from vanilla Holder.
 */
public interface IHolderExtension<T> extends IWithData<T> {
    private Holder<T> self() {
        return (Holder<T>) this;
    }

    @Nullable
    @Override
    default <D> D getData(DataMapType<T, D> type) {
        return net.neoforged.neoforge.internal.fabric.DataMapManager.getData(self(), type);
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

    /**
     * NeoForge name for the holder itself when used as a delegate.
     */
    default Holder<T> getDelegate() {
        return self();
    }
}
