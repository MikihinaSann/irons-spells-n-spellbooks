package net.neoforged.neoforge.registries.holdersets;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

/**
 * Stand-in for NeoForge's patched {@code RegistryOps.retrieveRegistryLookup}.
 * Implemented against the public {@link RegistryOps#owner(ResourceKey)} API —
 * the lifecycle variant from NeoForge isn't observable through vanilla ops.
 */
public final class RegistryOpsCompat {

    private RegistryOpsCompat() {}

    public static <E> MapCodec<HolderLookup.RegistryLookup<E>> retrieveRegistryLookup(ResourceKey<? extends Registry<? extends E>> resourceKey) {
        return ExtraCodecs.retrieveContext(ops -> {
            if (!(ops instanceof RegistryOps<?> registryOps))
                return DataResult.error(() -> "Not a registry ops");

            return registryOps.owner(resourceKey).map(owner -> {
                if (!(owner instanceof HolderLookup.RegistryLookup<E> registryLookup))
                    return DataResult.<HolderLookup.RegistryLookup<E>>error(() -> "Found holder getter but was not a registry lookup for " + resourceKey);

                return DataResult.success(registryLookup);
            }).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + resourceKey));
        });
    }
}
