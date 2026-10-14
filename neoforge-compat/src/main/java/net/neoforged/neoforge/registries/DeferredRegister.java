package net.neoforged.neoforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fabric-compatible replacement for NeoForge's {@code DeferredRegister}.
 * Queues registrations; they are processed when {@link #register(IEventBus)} is drained
 * through {@link #drainAll()} during mod init.
 */
public class DeferredRegister<R> {
    /** Global queue of drains, in register() order — mirrors NeoForge firing RegisterEvent per registry. */
    private static final List<Runnable> PENDING = new ArrayList<>();

    public static void drainAll() {
        for (Runnable r : List.copyOf(PENDING)) {
            r.run();
        }
        PENDING.clear();
    }

    private final ResourceKey<? extends Registry<R>> registryKey;
    private final String modId;
    private final List<DeferredHolder<R, ? extends R>> entries = new ArrayList<>();
    private final java.util.Map<net.minecraft.resources.ResourceLocation, net.minecraft.resources.ResourceLocation> pendingAliases = new java.util.HashMap<>();
    private boolean accepted = false;

    protected DeferredRegister(ResourceKey<? extends Registry<R>> registryKey, String modId) {
        this.registryKey = registryKey;
        this.modId = modId;
    }

    public static <R> DeferredRegister<R> create(ResourceKey<? extends Registry<R>> registryKey, String modid) {
        return new DeferredRegister<>(registryKey, modid);
    }

    public static <R> DeferredRegister<R> create(Registry<R> registry, String modid) {
        return new DeferredRegister<>(registry.key(), modid);
    }

    public String getNamespace() {
        return modId;
    }

    public ResourceKey<? extends Registry<R>> getRegistryKey() {
        return registryKey;
    }

    /** Factory hook so typed subclasses (Blocks/Items) can produce typed holders. */
    protected <I extends R> DeferredHolder<R, I> createHolder(ResourceLocation location, Supplier<I> supplier) {
        return new DeferredHolder<>(registryKey, location, supplier);
    }

    public <I extends R> DeferredHolder<R, I> register(String name, Supplier<? extends I> supplier) {
        checkMutable();
        @SuppressWarnings("unchecked")
        Supplier<I> typed = (Supplier<I>) supplier;
        DeferredHolder<R, I> holder = createHolder(ResourceLocation.fromNamespaceAndPath(modId, name), typed);
        holder.setOwner(this);
        entries.add(holder);
        return holder;
    }

    public <I extends R> DeferredHolder<R, I> register(String name, Function<ResourceLocation, I> factory) {
        checkMutable();
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modId, name);
        DeferredHolder<R, I> holder = createHolder(location, () -> factory.apply(location));
        holder.setOwner(this);
        entries.add(holder);
        return holder;
    }

    public Set<DeferredHolder<R, ? extends R>> getEntries() {
        return new LinkedHashSet<>(entries);
    }

    /**
     * Queues this register for draining during mod init. The bus argument is a stub;
     * actual registration happens when {@link #drainAll()} is invoked by the entrypoint.
     */
    public void register(IEventBus bus) {
        PENDING.add(this::accept);
    }

    /** Immediately registers (legacy call sites that never queued). */
    public void register() {
        accept();
    }

    private void checkMutable() {
        if (accepted) {
            throw new IllegalStateException("Cannot register after accept() has been called");
        }
    }

    public void accept() {
        if (accepted) {
            return;
        }
        accepted = true;
        Registry<R> registry = getRegistry();
        for (DeferredHolder<R, ?> entry : entries) {
            entry.bind(registry);
        }
        // Aliases: NeoForge's Registry.addAlias — a lookup redirection to the
        // target id, applied by MappedRegistryAliasMixin. Registering the same
        // object under a second id trips vanilla's intrusive-holder check.
        pendingAliases.forEach((alias, target) -> {
            if (registry.containsKey(target)) {
                RegistryAliases.addAlias(registry, alias, target);
            }
        });
        pendingAliases.clear();
    }

    private static final java.util.Map<ResourceKey<?>, net.minecraft.core.MappedRegistry<?>> DETACHED = new java.util.HashMap<>();

    /**
     * Finds the registry (builtin or a NewRegistryEvent-created custom registry)
     * for the given registry-key name, or null if it doesn't exist.
     */
    @SuppressWarnings("unchecked")
    static <R> net.minecraft.core.Registry<R> lookupRegistry(ResourceLocation registryKeyName) {
        net.minecraft.core.Registry<?> registry = BuiltInRegistries.REGISTRY.get(registryKeyName);
        if (registry != null) {
            return (net.minecraft.core.Registry<R>) registry;
        }
        net.minecraft.core.MappedRegistry<?> custom = DETACHED.get(net.minecraft.resources.ResourceKey.createRegistryKey(registryKeyName));
        return (net.minecraft.core.Registry<R>) custom;
    }
    private static final org.slf4j.Logger DR_LOG = org.slf4j.LoggerFactory.getLogger("DeferredRegister");

    @SuppressWarnings("unchecked")
    private Registry<R> getRegistry() {
        // Custom registries (FabricRegistryBuilder.buildAndRegister) are also
        // registered into the root registry, so this covers both cases.
        Registry<?> registry = BuiltInRegistries.REGISTRY.get(registryKey.location());
        if (registry == null) {
            // Datapack registries (worldgen/*, damage_type, etc.) have no static
            // instance — their contents load from datapack JSONs. Entries queued
            // here bind to a detached registry so DeferredHolders stay usable.
            registry = DETACHED.computeIfAbsent(registryKey,
                    k -> new net.minecraft.core.MappedRegistry<>((ResourceKey<Registry<R>>) registryKey,
                            com.mojang.serialization.Lifecycle.stable()));
            if (!entries.isEmpty()) {
                DR_LOG.warn("DeferredRegister for datapack registry {} has {} code-registered entries; " +
                        "they are NOT visible in-world unless matching datapack JSONs exist",
                        registryKey.location(), entries.size());
            }
        }
        return (Registry<R>) registry;
    }

    /** NeoForge-style callback hook for places that iterate entries with a consumer. */
    public void forEach(Consumer<? super DeferredHolder<R, ?>> consumer) {
        entries.forEach(consumer);
    }

    /** NeoForge's {@code DeferredRegister.DataComponents} factory (data-component-type registry). */
    public static DataComponents createDataComponents(ResourceKey<? extends Registry<net.minecraft.core.component.DataComponentType<?>>> registryKey, String modid) {
        return new DataComponents(registryKey, modid);
    }

    public static Blocks createBlocks(String modid) {
        return new Blocks(modid);
    }

    public static Items createItems(String modid) {
        return new Items(modid);
    }

    /** DeferredRegister specialized for blocks — produces {@link DeferredBlock} holders. */
    public static class Blocks extends DeferredRegister<Block> {
        private Blocks(String modId) {
            super(Registries.BLOCK, modId);
        }

        @Override
        protected <I extends Block> DeferredHolder<Block, I> createHolder(ResourceLocation location, Supplier<I> supplier) {
            return new DeferredBlock<>(Registries.BLOCK, location, supplier);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends Block> DeferredBlock<I> register(String name, Supplier<? extends I> supplier) {
            return (DeferredBlock<I>) (DeferredHolder<Block, I>) super.register(name, supplier);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends Block> DeferredBlock<I> register(String name, Function<ResourceLocation, I> factory) {
            return (DeferredBlock<I>) (DeferredHolder<Block, I>) super.register(name, factory);
        }

        public DeferredBlock<Block> registerSimpleBlock(String name, BlockBehaviour.Properties properties) {
            return register(name, () -> new Block(properties));
        }

        public <I extends Block> DeferredBlock<I> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends I> func, Supplier<BlockBehaviour.Properties> properties) {
            return register(name, () -> func.apply(properties.get()));
        }
    }

    /** DeferredRegister specialized for items — produces {@link DeferredItem} holders. */
    public static class Items extends DeferredRegister<Item> {
        private Items(String modId) {
            super(Registries.ITEM, modId);
        }

        @Override
        protected <I extends Item> DeferredHolder<Item, I> createHolder(ResourceLocation location, Supplier<I> supplier) {
            return new DeferredItem<>(Registries.ITEM, location, supplier);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier) {
            return (DeferredItem<I>) (DeferredHolder<Item, I>) super.register(name, supplier);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends Item> DeferredItem<I> register(String name, Function<ResourceLocation, I> factory) {
            return (DeferredItem<I>) (DeferredHolder<Item, I>) super.register(name, factory);
        }

        public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
            return registerItem(name, func, Item.Properties::new);
        }

        public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
            return register(name, () -> func.apply(properties.get()));
        }

        public DeferredItem<Item> registerSimpleItem(String name) {
            return registerItem(name, Item::new);
        }

        public DeferredItem<Item> registerSimpleItem(String name, Item.Properties properties) {
            return register(name, () -> new Item(properties));
        }
    }

    /** DeferredRegister specialized for data component types. */
    public static class DataComponents extends DeferredRegister<net.minecraft.core.component.DataComponentType<?>> {
        private DataComponents(ResourceKey<? extends Registry<net.minecraft.core.component.DataComponentType<?>>> registryKey, String modId) {
            super(registryKey, modId);
        }

        public <D> DeferredHolder<net.minecraft.core.component.DataComponentType<?>, net.minecraft.core.component.DataComponentType<D>> registerComponentType(
                String name,
                java.util.function.UnaryOperator<net.minecraft.core.component.DataComponentType.Builder<D>> builder) {
            return register(name, () -> builder.apply(net.minecraft.core.component.DataComponentType.builder()).build());
        }
    }

    /** NeoForge's alias: redirects lookups of {@code aliasId} to {@code targetId}. */
    public void addAlias(net.minecraft.resources.ResourceLocation aliasId,
                         net.minecraft.resources.ResourceLocation targetId) {
        this.pendingAliases.put(aliasId, targetId);
    }
}
