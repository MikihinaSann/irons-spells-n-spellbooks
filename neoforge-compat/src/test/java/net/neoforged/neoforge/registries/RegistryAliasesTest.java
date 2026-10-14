package net.neoforged.neoforge.registries;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

/**
 * RegistryAliases resolution semantics: direct, chained, cyclic, absent-target
 * and wrong-registry-key gating. Mixed-in MappedRegistry methods are exercised
 * at runtime; this covers the shared utility behavior they delegate to.
 */
public class RegistryAliasesTest {

    private static final ResourceLocation OLD = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "memory_fragment");
    private static final ResourceLocation NEW = ResourceLocation.fromNamespaceAndPath("familiarslib", "memory_fragment");
    private static final ResourceLocation MID = ResourceLocation.fromNamespaceAndPath("familiarslib", "intermediate");
    private static final ResourceLocation ITEM_KEY = ResourceLocation.fromNamespaceAndPath("minecraft", "item");
    private static final ResourceLocation BLOCK_KEY = ResourceLocation.fromNamespaceAndPath("minecraft", "block");

    @SuppressWarnings("unchecked")
    private static MappedRegistry<Object> registry(ResourceLocation registryId) {
        return new MappedRegistry<>(
                ResourceKey.createRegistryKey(registryId),
                com.mojang.serialization.Lifecycle.stable(), false);
    }

    @Test
    void directAliasResolvesToTarget() {
        Registry<?> reg = registry(ITEM_KEY);
        RegistryAliases.addAlias(reg, OLD, NEW);
        assertEquals(NEW, RegistryAliases.resolve(reg, OLD));
        assertNull(RegistryAliases.resolve(reg, NEW), "target itself is not an alias");
        assertNull(RegistryAliases.resolve(reg, ResourceLocation.fromNamespaceAndPath("x", "y")));
    }

    @Test
    void chainedAliasResolvesToFinalTarget() {
        Registry<?> reg = registry(ITEM_KEY);
        RegistryAliases.addAlias(reg, OLD, MID);
        RegistryAliases.addAlias(reg, MID, NEW);
        assertEquals(NEW, RegistryAliases.resolve(reg, OLD), "chain A->B->C resolves to C");
        assertEquals(NEW, RegistryAliases.resolve(reg, MID));
    }

    @Test
    void absentTargetStillResolvesLookupId() {
        // resolve() returns the mapped target id even when nothing is registered
        // under it — presence is checked by the registry call site.
        Registry<?> reg = registry(ITEM_KEY);
        ResourceLocation absent = ResourceLocation.fromNamespaceAndPath("familiarslib", "absent");
        RegistryAliases.addAlias(reg, OLD, absent);
        assertEquals(absent, RegistryAliases.resolve(reg, OLD));
    }

    @Test
    void cyclicAliasRejectedAtAddition() {
        Registry<?> reg = registry(ITEM_KEY);
        RegistryAliases.addAlias(reg, OLD, MID);
        assertThrows(IllegalArgumentException.class,
                () -> RegistryAliases.addAlias(reg, MID, OLD), "A->B then B->A must be rejected");
        assertThrows(IllegalArgumentException.class,
                () -> RegistryAliases.addAlias(reg, OLD, OLD), "self-alias must be rejected");
        // the rejected alias must not have been stored
        assertEquals(MID, RegistryAliases.resolve(reg, OLD));
        assertNull(RegistryAliases.resolve(reg, MID));
    }

    @SuppressWarnings("unchecked")
    @Test
    void wrongRegistryKeyDoesNotResolve() {
        Registry<?> items = registry(ITEM_KEY);
        RegistryAliases.addAlias(items, OLD, NEW);
        ResourceKey<Object> itemKey = ResourceKey.create(
                (ResourceKey<? extends Registry<Object>>) (ResourceKey<?>) ResourceKey.createRegistryKey(ITEM_KEY), OLD);
        ResourceKey<Object> blockKey = ResourceKey.create(
                (ResourceKey<? extends Registry<Object>>) (ResourceKey<?>) ResourceKey.createRegistryKey(BLOCK_KEY), OLD);
        assertEquals(NEW, RegistryAliases.resolve(items, itemKey));
        assertNull(RegistryAliases.resolve(items, blockKey),
                "a same-named key on another registry must not resolve");
    }

    @Test
    void aliasesArePerRegistry() {
        Registry<?> items = registry(ITEM_KEY);
        Registry<Object> blocks = registry(BLOCK_KEY);
        RegistryAliases.addAlias(items, OLD, NEW);
        assertNull(RegistryAliases.resolve(blocks, OLD), "alias table is scoped per-registry");
    }

    @Test
    void realTargetLookupUnaffected() {
        MappedRegistry<Object> items = registry(ITEM_KEY);
        ResourceKey<Object> real = ResourceKey.create(
                (ResourceKey<? extends Registry<Object>>) (ResourceKey<?>) ResourceKey.createRegistryKey(ITEM_KEY), NEW);
        items.register(real, new Object(), net.minecraft.core.RegistrationInfo.BUILT_IN);
        items.freeze();
        RegistryAliases.addAlias(items, OLD, NEW);
        // target id still serializes canonically — alias created no duplicate holder
        assertSame(items.get(NEW), items.get(real));
        assertEquals(NEW, items.getKey(items.get(real)));
    }
}
