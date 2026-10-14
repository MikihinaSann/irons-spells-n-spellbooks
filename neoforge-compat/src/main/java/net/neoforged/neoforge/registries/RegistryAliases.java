package net.neoforged.neoforge.registries;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * NeoForge's {@code Registry.addAlias} — a lookup redirection: the alias id resolves
 * to the entry registered under the target id without binding a second registry
 * holder (which vanilla's intrusive-holder check forbids). Consulted by
 * MappedRegistryAliasMixin on get/getHolder/containsKey.
 * <p>
 * Aliases may chain (A→B, B→C resolves A to C). Cycles are rejected at
 * addition time; a defensive chain bound also guards resolution.
 */
public final class RegistryAliases {
    /** Hard bound on alias chain depth — NeoForge aliases are direct, chains are ours. */
    private static final int MAX_CHAIN = 32;
    private static final Map<Registry<?>, Map<ResourceLocation, ResourceLocation>> ALIASES = new IdentityHashMap<>();

    /**
     * Registers {@code aliasId} as an alias for {@code targetId} on {@code registry}.
     * Rejects self-aliases and alias cycles.
     */
    public static void addAlias(Registry<?> registry, ResourceLocation aliasId, ResourceLocation targetId) {
        Map<ResourceLocation, ResourceLocation> map =
                ALIASES.computeIfAbsent(registry, r -> new ConcurrentHashMap<>());
        if (aliasId.equals(targetId)) {
            throw new IllegalArgumentException("Alias " + aliasId + " cannot target itself");
        }
        // Walk from the target through existing links — reaching the alias id
        // would close a cycle (e.g. adding A->B when B->A exists).
        ResourceLocation cur = targetId;
        for (int i = 0; i < MAX_CHAIN; i++) {
            ResourceLocation next = map.get(cur);
            if (next == null) {
                break;
            }
            if (next.equals(aliasId)) {
                throw new IllegalArgumentException(
                        "Alias " + aliasId + " -> " + targetId + " would create a cycle");
            }
            cur = next;
        }
        map.put(aliasId, targetId);
    }

    /**
     * Nullable — the final target id the alias (chain) resolves to, or null when
     * {@code id} is not an alias. Bounded; a residual cycle yields null.
     */
    public static ResourceLocation resolve(Registry<?> registry, ResourceLocation id) {
        Map<ResourceLocation, ResourceLocation> map = ALIASES.get(registry);
        if (map == null) {
            return null;
        }
        ResourceLocation cur = id;
        ResourceLocation resolved = null;
        for (int i = 0; i < MAX_CHAIN; i++) {
            ResourceLocation next = map.get(cur);
            if (next == null) {
                return resolved;
            }
            resolved = next;
            cur = next;
        }
        return null;
    }

    /**
     * Key-aware resolve: only applies when {@code key} actually names
     * {@code registry}; a same-named alias in another registry never applies.
     */
    public static ResourceLocation resolve(Registry<?> registry, net.minecraft.resources.ResourceKey<?> key) {
        return key.isFor(registry.key()) ? resolve(registry, key.location()) : null;
    }

    private RegistryAliases() {
    }
}
