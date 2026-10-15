package net.neoforged.neoforge.internal.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistExecutor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * NeoForge-compat lifecycle driver. Every Fabric mod that carries a
 * NeoForge-style port declares entrypoints:
 *
 * <ul>
 *   <li>{@code "neoforge"} - its {@code @Mod} classes (constructed with
 *   FML-style constructor matching)</li>
 *   <li>{@code "neoforge:client"} - {@code @Mod(dist=CLIENT)} classes</li>
 *   <li>{@code "neoforge:setup"} - post-lifecycle hooks (ModInitializer or
 *   Runnable) invoked once the generic lifecycle has run</li>
 *   <li>{@code "neoforge:client_setup"} - same on the client</li>
 * </ul>
 *
 * <p>{@link #MOD_BUS} is the single shared mod bus (NeoForge's mod-event bus);
 * {@link NeoForge#EVENT_BUS} is the shared game bus. {@code @EventBusSubscriber}
 * classes are discovered automatically by scanning the root paths of every
 * Fabric mod that declares a {@code neoforge} entrypoint and registering each
 * matching class on both buses - identical to the previous manual {@code sub()}
 * semantics.</p>
 */
public class NeoForgeCompat implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");

    /** The single shared NeoForge-style mod event bus. */
    public static final FabricEventBus MOD_BUS = new FabricEventBus();

    /** The running dedicated/integrated server (set from SERVER_STARTED). */
    public static MinecraftServer SERVER;

    private static final Set<String> REGISTERED_SUBSCRIBERS = new HashSet<>();
    private static final List<net.neoforged.fml.ModContainer> MOD_CONTAINERS = new ArrayList<>();

    /** All constructed NeoForge-style mod containers, in construction order. */
    public static List<net.neoforged.fml.ModContainer> getModContainers() {
        return MOD_CONTAINERS;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("neoforge_compat boot envType={}", FabricLoader.getInstance().getEnvironmentType());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
            net.neoforged.neoforge.server.ServerLifecycleHooks.setCurrentServer(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SERVER = null;
            net.neoforged.neoforge.server.ServerLifecycleHooks.setCurrentServer(null);
        });
        net.neoforged.neoforge.common.util.LogicalSidedProvider.setServer(() -> SERVER);

        List<net.fabricmc.loader.api.ModContainer> mods = discoverNeoForgeMods();
        // Enum constants must exist before any mod class is initialized —
        // subscriber scanning can trigger clinit on classes whose statics do
        // Enum#valueOf on the extended constants.
        EnumExtensionLoader.process(mods);
        scanSubscribers(mods, DistExecutor.currentDist() == Dist.DEDICATED_SERVER);
        constructMods(mods, "neoforge");
        runCommonLifecycle();
        runHooks("neoforge:setup");
    }

    /** Fabric mods that declare any {@code neoforge}-style entrypoint, in loader order. */
    static List<net.fabricmc.loader.api.ModContainer> discoverNeoForgeMods() {
        List<net.fabricmc.loader.api.ModContainer> out = new ArrayList<>();
        for (net.fabricmc.loader.api.ModContainer container : FabricLoader.getInstance().getAllMods()) {
            if (!entrypointClassNames(container, "neoforge").isEmpty()
                    || !entrypointClassNames(container, "neoforge:client").isEmpty()
                    || !entrypointClassNames(container, "neoforge:setup").isEmpty()
                    || !entrypointClassNames(container, "neoforge:client_setup").isEmpty()) {
                out.add(container);
            }
        }
        return out;
    }

    /**
     * ASM scan of each NeoForge-style Fabric mod's root paths for
     * {@code @EventBusSubscriber} classes.
     */
    static void scanSubscribers(List<net.fabricmc.loader.api.ModContainer> mods, boolean dedicatedServer) {
        int found = 0;
        java.util.Map<String, Integer> perMod = new java.util.LinkedHashMap<>();
        for (net.fabricmc.loader.api.ModContainer container : mods) {
            String fabricModId = container.getMetadata().getId();
            Set<String> localModIds = collectNeoForgeModIds(container);
            int local = 0;
            // loom.mods dev-injected mods report root "/" — their real class dirs
            // live in the mod origin's paths. ModOrigin.getPaths() is only valid
            // for Kind.PATH (NESTED/UNKNOWN throw) — fall back to root paths.
            java.util.List<Path> roots;
            if (container.getOrigin().getKind() == net.fabricmc.loader.api.metadata.ModOrigin.Kind.PATH) {
                roots = container.getOrigin().getPaths().stream()
                        .map(rp -> rp.toAbsolutePath().normalize())
                        .filter(p -> !p.toString().equals(java.io.File.separator))
                        .toList();
            } else {
                roots = List.of();
            }
            if (roots.isEmpty()) {
                roots = container.getRootPaths();
            }
            roots = new ArrayList<>(new java.util.LinkedHashSet<>(roots));
            for (Path root : roots) {
                LOGGER.info("neoforge_compat scan mod {} root {}", fabricModId, root);
                for (String className : listClasses(root)) {
                    SubscriberInfo info = readSubscriberInfo(root, className);
                    if (info == null) {
                        continue;
                    }
                    // modid filter: empty -> owning mod; otherwise must match the
                    // owning fabric mod id or a neoforge modid it declares.
                    if (!info.modid.isEmpty() && !localModIds.contains(info.modid)) {
                        continue;
                    }
                    // dist filter: Dist.CLIENT registers only on the client,
                    // Dist.DEDICATED_SERVER only on a dedicated server.
                    if (info.clientOnly && dedicatedServer) {
                        continue;
                    }
                    if (info.serverOnly && !dedicatedServer) {
                        continue;
                    }
                    if (registerSubscriber(className, info.modid.isEmpty() ? fabricModId : info.modid)) {
                        found++;
                        local++;
                    }
                }
            }
            perMod.put(fabricModId, local);
        }
        LOGGER.info("neoforge_compat subscriber scan registered {} classes from {} mod(s): {}",
                found, mods.size(), perMod);
    }

    /** Mod ids declared by {@code neoforge}/{@code neoforge:client} entrypoints plus the fabric mod id. */
    private static Set<String> collectNeoForgeModIds(net.fabricmc.loader.api.ModContainer container) {
        Set<String> ids = new HashSet<>();
        ids.add(container.getMetadata().getId());
        for (String key : List.of("neoforge", "neoforge:client")) {
            for (String className : entrypointClassNames(container, key)) {
                try {
                    Class<?> clazz = Class.forName(className);
                    var mod = clazz.getAnnotation(net.neoforged.fml.common.Mod.class);
                    if (mod != null) {
                        ids.add(!mod.modid().isEmpty() ? mod.modid() : mod.value());
                    }
                } catch (Throwable ignored) {
                    // classes with missing optional deps can't be loaded here; the
                    // scanner only needs the fabric mod id in that case.
                }
            }
        }
        return ids;
    }

    static boolean isSubscriberRegistered(String className) {
        return REGISTERED_SUBSCRIBERS.contains(className);
    }

    /** Register a subscriber class on both buses once. Mirrors the old sub() semantics. */
    static boolean registerSubscriber(String className, String owner) {
        if (!REGISTERED_SUBSCRIBERS.add(className)) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName(className);
            MOD_BUS.register(clazz, owner);
            NeoForge.EVENT_BUS.register(clazz);
            return true;
        } catch (Throwable t) {
            LOGGER.info("neoforge_compat skipping subscriber {} - class load failed", className, t);
            return false;
        }
    }

    record SubscriberInfo(String modid, boolean clientOnly, boolean serverOnly) {
    }

    /** Read @EventBusSubscriber from a .class file without loading it. */
    static SubscriberInfo readSubscriberInfo(Path root, String className) {
        Path classFile = Files.isDirectory(root)
                ? root.resolve(className.replace('.', '/') + ".class")
                : null;
        try (InputStream in = classFile != null
                ? Files.newInputStream(classFile)
                : openJarEntry(root, className)) {
            if (in == null) {
                return null;
            }
            String[] modid = {""};
            boolean[] clientOnly = {false};
            boolean[] serverOnly = {false};
            boolean[] found = {false};
            ClassReader reader = new ClassReader(in);
            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (!descriptor.equals("Lnet/neoforged/fml/common/EventBusSubscriber;")) {
                        return super.visitAnnotation(descriptor, visible);
                    }
                    found[0] = true;
                    return new AnnotationVisitor(Opcodes.ASM9, super.visitAnnotation(descriptor, visible)) {
                        @Override
                        public void visit(String name, Object value) {
                            if ("modid".equals(name) && value instanceof String s) {
                                modid[0] = s;
                            }
                            super.visit(name, value);
                        }

                        @Override
                        public void visitEnum(String name, String descriptor, String value) {
                            markDist(value);
                            super.visitEnum(name, descriptor, value);
                        }

                        @Override
                        public AnnotationVisitor visitArray(String name) {
                            if ("value".equals(name) || "dist".equals(name)) {
                                return new AnnotationVisitor(Opcodes.ASM9, super.visitArray(name)) {
                                    @Override
                                    public void visitEnum(String name2, String descriptor, String value) {
                                        markDist(value);
                                        super.visitEnum(name2, descriptor, value);
                                    }
                                };
                            }
                            return super.visitArray(name);
                        }

                        private void markDist(String value) {
                            if ("CLIENT".equals(value)) {
                                clientOnly[0] = true;
                            } else if ("DEDICATED_SERVER".equals(value)) {
                                serverOnly[0] = true;
                            }
                        }
                    };
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return found[0] ? new SubscriberInfo(modid[0], clientOnly[0], serverOnly[0]) : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static InputStream openJarEntry(Path jarPath, String className) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry entry = jar.getJarEntry(className.replace('.', '/') + ".class");
            if (entry == null) {
                return null;
            }
            // read eagerly — the returned stream must not outlive the JarFile
            try (InputStream src = jar.getInputStream(entry)) {
                return new java.io.ByteArrayInputStream(src.readAllBytes());
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static List<String> listClasses(Path root) {
        List<String> classes = new ArrayList<>();
        try {
            if (Files.isDirectory(root)) {
                try (var walk = Files.walk(root)) {
                    walk.filter(p -> p.toString().endsWith(".class"))
                            .forEach(p -> classes.add(
                                    root.relativize(p).toString()
                                            .replace(java.io.File.separatorChar, '.')
                                            .replaceAll("\\.class$", "")));
                }
            } else if (root.toString().endsWith(".jar")) {
                try (JarFile jar = new JarFile(root.toFile())) {
                    jar.stream()
                            .filter(e -> e.getName().endsWith(".class") && !e.getName().equals("module-info.class"))
                            .forEach(e -> classes.add(e.getName()
                                    .replace('/', '.')
                                    .replaceAll("\\.class$", "")));
                }
            }
        } catch (Throwable t) {
            LOGGER.debug("Failed to enumerate classes under {}", root, t);
        }
        return classes;
    }

    /**
     * Construct every {@code "neoforge"} entrypoint's @Mod class with FML-style
     * constructor matching: the chosen constructor's params may be any subset of
     * (IEventBus, net.neoforged.fml.ModContainer, Dist). A class that fails to
     * load (e.g. optional dependency absent) is skipped with a debug log - the
     * merged conditional mods (epicfight, tacz) rely on this.
     */
    static void constructMods(List<net.fabricmc.loader.api.ModContainer> mods, String key) {
        for (net.fabricmc.loader.api.ModContainer fabric : mods) {
            for (String className : entrypointClassNames(fabric, key)) {
                constructMod(fabric, className);
            }
        }
    }

    static void constructMod(net.fabricmc.loader.api.ModContainer fabric, String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (Throwable t) {
            LOGGER.debug("Skipping @Mod class {} - class load failed (optional dependency absent?)", className, t);
            return;
        }
        var modAnn = clazz.getAnnotation(net.neoforged.fml.common.Mod.class);
        String modid = modAnn == null ? fabric.getMetadata().getId()
                : (!modAnn.modid().isEmpty() ? modAnn.modid() : modAnn.value());
        // honor @Mod dist if present
        if (modAnn != null && !modAnn.dist().isEmpty()) {
            String dist = modAnn.dist();
            boolean onServer = DistExecutor.currentDist() == Dist.DEDICATED_SERVER;
            if (("DEDICATED_SERVER".equals(dist) && !onServer) || ("CLIENT".equals(dist) && onServer)) {
                return;
            }
        }
        net.neoforged.fml.ModContainer container =
                new net.neoforged.fml.ModContainer(fabric, modid);
        try {
            construct(clazz, MOD_BUS.scoped(modid), container);
            MOD_CONTAINERS.add(container);
            LOGGER.info("neoforge_compat constructed @Mod {} (modid {})", className, modid);
        } catch (Throwable t) {
            LOGGER.error("Skipping @Mod class {} - construction failed", className, t);
        }
    }

    /** FML-style ctor matching: any subset of (IEventBus, ModContainer, Dist). */
    private static void construct(Class<?> clazz, IEventBus bus,
                                  net.neoforged.fml.ModContainer container) throws Exception {
        for (var ctor : clazz.getDeclaredConstructors()) {
            Object[] args = new Object[ctor.getParameterCount()];
            boolean ok = true;
            for (int i = 0; i < args.length; i++) {
                Class<?> p = ctor.getParameterTypes()[i];
                if (IEventBus.class.isAssignableFrom(p)) {
                    args[i] = bus;
                } else if (net.neoforged.fml.ModContainer.class.isAssignableFrom(p)) {
                    args[i] = container;
                } else if (Dist.class.isAssignableFrom(p)) {
                    args[i] = DistExecutor.currentDist() == Dist.DEDICATED_SERVER ? Dist.DEDICATED_SERVER : Dist.CLIENT;
                } else {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                ctor.setAccessible(true);
                ctor.newInstance(args);
                return;
            }
        }
        throw new NoSuchMethodException("No FML-compatible constructor on " + clazz.getName());
    }

    /** Invoke {@code neoforge:setup}/{@code neoforge:client_setup} hook classes. */
    static void runHooks(String key) {
        for (var container : FabricLoader.getInstance().getAllMods()) {
            for (String className : entrypointClassNames(container, key)) {
                try {
                    Object hook = Class.forName(className).getDeclaredConstructor().newInstance();
                    if (hook instanceof ModInitializer initializer) {
                        initializer.onInitialize();
                    } else if (hook instanceof net.fabricmc.api.ClientModInitializer clientInit) {
                        clientInit.onInitializeClient();
                    } else if (hook instanceof Runnable runnable) {
                        runnable.run();
                    }
                } catch (Throwable t) {
                    LOGGER.error("neoforge setup hook {} failed", className, t);
                }
            }
        }
    }

    /**
     * Read the class-name strings declared under {@code entrypoints.<key>} in a
     * mod's fabric.mod.json, parsed straight from the mod's root path. (Loader's
     * getEntrypointContainers would instantiate each class with a zero-arg
     * ctor - wrong for our FML-style @Mod ctors.)
     */
    private static List<String> entrypointClassNames(net.fabricmc.loader.api.ModContainer container, String key) {
        List<String> out = new ArrayList<>();
        for (Path root : container.getRootPaths()) {
            Path fmj = root.resolve("fabric.mod.json");
            if (!Files.isRegularFile(fmj)) {
                continue;
            }
            try (var reader = Files.newBufferedReader(fmj)) {
                var json = com.google.gson.JsonParser.parseReader(reader);
                if (!json.isJsonObject() || !json.getAsJsonObject().has("entrypoints")) {
                    continue;
                }
                var eps = json.getAsJsonObject().getAsJsonObject("entrypoints");
                if (!eps.has(key)) {
                    continue;
                }
                for (var item : eps.getAsJsonArray(key)) {
                    if (item.isJsonPrimitive()) {
                        out.add(item.getAsString());
                    } else if (item.isJsonObject() && item.getAsJsonObject().has("value")) {
                        out.add(item.getAsJsonObject().get("value").getAsString());
                    }
                }
            } catch (Throwable t) {
                LOGGER.debug("Failed to read fabric.mod.json under {}", root, t);
            }
        }
        return out;
    }

    static boolean isServer() {
        return DistExecutor.currentDist() == Dist.DEDICATED_SERVER;
    }

    /**
     * The generic NeoForge mod lifecycle - runs once, after all @Mod classes are
     * constructed. Order matches FML's: registry creation, registrations, then
     * setup, then the game-side drains and runtime bridges.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void runCommonLifecycle() {
        NeoForgeMod.registerAttributes();
        NeoForgeMod.registerIngredientTypes();
        NeoForgeMod.registerBiomeModifierSerializers();
        NeoForgeMod.registerStructureModifierSerializers();
        NeoForgeMod.registerConditionCodecs();
        NeoForgeMod.registerFluidTypes();
        MOD_BUS.post(new NewRegistryEvent());
        var datapackRegistryEvent = new net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry();
        MOD_BUS.post(datapackRegistryEvent);
        datapackRegistryEvent.finish();
        // NeoForge's own datapack registries (NF registers these in NeoForgeMod's
        // DataPackRegistryEvent.NewRegistry listener). Server-only like upstream;
        // the elementsDirPath mixin resolves data/<ns>/neoforge/<key>/.
        net.fabricmc.fabric.api.event.registry.DynamicRegistries.register(
                net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                com.mojang.serialization.Codec.lazyInitialized(() ->
                        net.neoforged.neoforge.registries.NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS
                                .byNameCodec()
                                .dispatch("type", net.neoforged.neoforge.common.world.BiomeModifier::codec,
                                        java.util.function.Function.identity())));
        net.fabricmc.fabric.api.event.registry.DynamicRegistries.register(
                net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS,
                com.mojang.serialization.Codec.lazyInitialized(() ->
                        net.neoforged.neoforge.registries.NeoForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS
                                .byNameCodec()
                                .dispatch("type", net.neoforged.neoforge.common.world.StructureModifier::codec,
                                        java.util.function.Function.identity())));
        java.util.Map<net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<?>>,
                java.util.Map<net.minecraft.resources.ResourceLocation,
                        net.neoforged.neoforge.registries.datamaps.DataMapType<?, ?>>> dataMapAttachments = new java.util.HashMap<>();
        net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent dataMapEvent =
                new net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent(dataMapAttachments);
        MOD_BUS.post(dataMapEvent);
        dataMapAttachments.values().forEach(map -> map.forEach((id, type) ->
                net.minecraft.core.Registry.register(
                        net.neoforged.neoforge.registries.NeoForgeRegistries.DATA_MAP_TYPES, id, type)));
        DeferredRegister.drainAll();
        net.neoforged.neoforge.common.DeferredSpawnEggItem.registerAllToByIdMap();
        net.minecraft.core.registries.BuiltInRegistries.REGISTRY.forEach(reg ->
                MOD_BUS.post(new net.neoforged.neoforge.registries.RegisterEvent(reg)));
        // Curios -> Trinkets: every registered ICurioItem becomes a Trinket.
        net.minecraft.core.registries.BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof top.theillusivec4.curios.api.type.capability.ICurioItem curioItem) {
                dev.emi.trinkets.api.TrinketsApi.registerTrinket(item,
                        new top.theillusivec4.curios.api.type.capability.TrinketCurioAdapter(curioItem));
            }
        });
        {
            net.neoforged.neoforge.event.ModifyDefaultComponentsEvent componentEvent =
                    new net.neoforged.neoforge.event.ModifyDefaultComponentsEvent();
            MOD_BUS.post(componentEvent);
            componentEvent.apply();
        }
        MOD_BUS.post(new FMLCommonSetupEvent());

        net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent payloadEvent =
                new net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent();
        MOD_BUS.post(payloadEvent);
        payloadEvent.getRegistrars().values().forEach(registrar ->
                registrar.getRegistrations().forEach(net.neoforged.neoforge.network.registration.PayloadRegistrar.Registration::register));
        net.neoforged.neoforge.network.registration.NetworkRegistry.drain();

        EntityAttributeCreationEvent attributeCreation = new EntityAttributeCreationEvent();
        MOD_BUS.post(attributeCreation);
        attributeCreation.getMap().forEach(FabricDefaultAttributeRegistry::register);

        EntityAttributeModificationEvent attributeModification = new EntityAttributeModificationEvent();
        MOD_BUS.post(attributeModification);
        attributeModification.getEntries().forEach(m -> mergeDefaultAttribute(m.type(), m.attribute(), m.value()));

        RegisterCapabilitiesEvent capabilities = new RegisterCapabilitiesEvent();
        MOD_BUS.post(capabilities);
        net.neoforged.neoforge.capabilities.CapabilityRegistry.drain(capabilities);

        MOD_BUS.post(new BlockEntityTypeAddBlocksEvent());

        RegisterSpawnPlacementsEvent placements = new RegisterSpawnPlacementsEvent();
        MOD_BUS.post(placements);
        drainSpawnPlacements(placements);

        MOD_BUS.post(new InterModEnqueueEvent());
        MOD_BUS.post(new InterModProcessEvent());

        MOD_BUS.post(new net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent());

        net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent loadComplete =
                new net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent();
        MOD_BUS.post(loadComplete);
        loadComplete.drainWork();

        AddReloadListenerEvent reloadEvent = new AddReloadListenerEvent();
        MOD_BUS.post(reloadEvent);
        NeoForge.EVENT_BUS.post(reloadEvent);
        int[] counter = {0};
        reloadEvent.getListeners().forEach(listener ->
                ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                        new net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener() {
                            private final net.minecraft.resources.ResourceLocation id =
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                            "neoforge", "reload_listener_" + (counter[0]++));

                            @Override
                            public net.minecraft.resources.ResourceLocation getFabricId() {
                                return id;
                            }

                            @Override
                            public java.util.concurrent.CompletableFuture<Void> reload(
                                    PreparationBarrier barrier, net.minecraft.server.packs.resources.ResourceManager manager,
                                    net.minecraft.util.profiling.ProfilerFiller prepProfiler,
                                    net.minecraft.util.profiling.ProfilerFiller reloadProfiler,
                                    java.util.concurrent.Executor backgroundExecutor, java.util.concurrent.Executor gameExecutor) {
                                return listener.reload(barrier, manager, prepProfiler, reloadProfiler,
                                        backgroundExecutor, gameExecutor);
                            }

                            @Override
                            public String getName() {
                                return listener.getName();
                            }
                        }));

        // ---- game-bus bridges ----
        net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents.TAGS_LOADED.register(
                (registries, client) ->
                        NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.TagsUpdatedEvent(
                                registries, client,
                                client ? net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED
                                        : net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)));
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        NeoForge.EVENT_BUS.post(new RegisterCommandsEvent(dispatcher, registryAccess, environment)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK.register(server ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.ServerTickEvent.Pre(server != null, server)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.ServerTickEvent.Post(server != null, server));
            net.neoforged.neoforge.common.WorldWorkerManager.tick();
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.LOAD.register((server, level) -> {
            for (var controller : TicketControllerRegistry.all()) {
                controller.onLevelLoad(level);
            }
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.UNLOAD.register((server, level) -> {
            for (var controller : TicketControllerRegistry.all()) {
                controller.onLevelUnload(level);
            }
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_WORLD_TICK.register(level ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.LevelTickEvent.Pre(level)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(level ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.LevelTickEvent.Post(level)));
        net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent(
                        (net.minecraft.world.item.alchemy.PotionBrewing.Builder) builder)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // NeoForge calls Entity#onAddedToLevel when an entity enters the level
            entity.onAddedToLevel();
            NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, world));
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            entity.onRemovedFromLevel();
            NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity, world));
        });
        // NF runs biome/structure modifiers inside handleServerAboutToStart,
        // before the ServerAboutToStartEvent is fired - register first so this
        // callback executes ahead of the event posts below.
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                BiomeModifierManager.applyAll(server.registryAccess()));
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStartingEvent(server)));
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.server.ServerAboutToStartEvent(server)));
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStartedEvent(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            // NeoForge fires LevelEvent.Unload for every level as the world
            // goes away — cleanup listeners depend on it.
            server.getAllLevels().forEach(level ->
                    NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.LevelEvent.Unload(level)));
            NeoForge.EVENT_BUS.post(new ServerStoppingEvent(server));
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStoppedEvent(server)));

        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(handler.getPlayer()));
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.OnDatapackSyncEvent(server.getPlayerList(), handler.getPlayer()));
            if (Boolean.getBoolean("iss.devAutoOp")) {
                com.mojang.authlib.GameProfile profile = handler.getPlayer().getGameProfile();
                if (!server.getPlayerList().isOp(profile)) {
                    server.getPlayerList().op(profile);
                }
            }
        });
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent(handler.getPlayer())));
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent(newPlayer, alive)));
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.COPY_FROM.register((original, clone, keepEverything) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone(clone, original, !keepEverything)));
        net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent(player, origin.dimension(), destination.dimension())));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.OnDatapackSyncEvent(server.getPlayerList(), null)));

        // --- interaction events ---
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, world, hand) -> {
            var event = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem(player, hand);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return new net.minecraft.world.InteractionResultHolder<>(event.getCancellationResult(), player.getItemInHand(hand));
            }
            return new net.minecraft.world.InteractionResultHolder<>(net.minecraft.world.InteractionResult.PASS, player.getItemInHand(hand));
        });
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            var event = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock(player, hand, hitResult.getBlockPos(), hitResult);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return event.getCancellationResult();
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            var pos = hitResult != null ? hitResult.getLocation() : entity.position();
            var event = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, pos);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return event.getCancellationResult();
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            var event = new net.neoforged.neoforge.event.entity.player.AttackEntityEvent(player, entity);
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled() ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            var event = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock(player, pos, direction);
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled() ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS;
        });

        // LeftClickEmpty: client posts the event locally, then notifies the
        // server through this C2S payload so listeners also run server-side.
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S()
                .register(EmptyClickPayload.TYPE, EmptyClickPayload.STREAM_CODEC);
        // S2C codec registration — the client adds the receiver in
        // NeoForgeCompatClient; the server only needs the type for encoding.
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C()
                .register(SpawnDataPayload.TYPE, SpawnDataPayload.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(
                EmptyClickPayload.TYPE, (payload, context) ->
                        context.server().execute(() -> NeoForge.EVENT_BUS.post(
                                new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty(
                                        context.player()))));
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            var event = new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(world, pos, state, player);
            NeoForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        });

        // BuildCreativeModeTabContentsEvent — posted once per tab while it populates.
        net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            var key = net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).orElse(null);
            var event = new net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent();
            event.setTab(tab);
            event.setTabKey(key);
            event.setParameters(entries.getContext());
            MOD_BUS.post(event);
            for (var addition : event.getAdditions()) {
                if (key != null && key.equals(addition.tabKey())) {
                    addition.action().accept(new net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent.TabOutput(entries));
                }
            }
        });

        // NeoForge's ItemStack.canElytraFly/elytraFlightTick correspond to FAPI's
        // EntityElytraEvents.CUSTOM: consult the chest stack's extension methods.
        // No early empty-stack bail — attribute/effect-driven flight (Apothic's
        // elytra_flight, ISS Angel Wings) answers through the stack methods even
        // for Items.AIR, and later-registered handlers (ISS) still run.
        net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            var chest = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            if (!chest.canElytraFly(entity)) {
                return false;
            }
            return !tickElytra || chest.elytraFlightTick(entity, entity.getFallFlyingTicks());
        });

        // builtin pack finders (server datapack hooks)
        AddPackFindersEvent serverPacks = new AddPackFindersEvent(PackType.SERVER_DATA);
        MOD_BUS.post(serverPacks);
        BuiltinPackSources.addAll(PackType.SERVER_DATA, serverPacks.getSources());

        // NeoForge biome/structure modifiers: loaded as datapack registries
        // (registered above via DynamicRegistries), applied before
        // ServerAboutToStartEvent and re-applied after /reload since the
        // datapack loader rebuilds the biome/structure registries fresh.
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, commandManager, success) -> {
            if (success) {
                BiomeModifierManager.applyAll(server.registryAccess());
            }
        });

        // global loot modifiers: neoforge:loot_table_id condition + JSON loader.
        // Newer datapacks use the common 'c' namespace (Alex's Caves 1.21.1).
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.LOOT_CONDITION_TYPE,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "loot_table_id"),
                net.neoforged.neoforge.common.conditions.LootTableIdCondition.TYPE);
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.LOOT_CONDITION_TYPE,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "loot_table_id"),
                net.neoforged.neoforge.common.conditions.LootTableIdCondition.TYPE);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                new net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener() {
                    @Override
                    public net.minecraft.resources.ResourceLocation getFabricId() {
                        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                "neoforge", "global_loot_modifiers");
                    }

                    @Override
                    public java.util.concurrent.CompletableFuture<Void> reload(
                            net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier barrier,
                            net.minecraft.server.packs.resources.ResourceManager manager,
                            net.minecraft.util.profiling.ProfilerFiller prepProfiler,
                            net.minecraft.util.profiling.ProfilerFiller reloadProfiler,
                            java.util.concurrent.Executor backgroundExecutor,
                            java.util.concurrent.Executor gameExecutor) {
                        return java.util.concurrent.CompletableFuture.runAsync(() ->
                                        GlobalLootModifierManager.collect(manager),
                                        backgroundExecutor)
                                .thenCompose(barrier::wait);
                    }
                });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) ->
                GlobalLootModifierManager.decode(server));
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                GlobalLootModifierManager.decode(server));
    }

    @SuppressWarnings("unchecked")
    private static void mergeDefaultAttribute(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity> type,
                                              net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
        var map = net.minecraft.world.entity.ai.attributes.DefaultAttributes.SUPPLIERS;
        var existing = map.get(type);
        var builder = net.minecraft.world.entity.ai.attributes.AttributeSupplier.builder();
        if (existing != null) {
            existing.instances.forEach((holder, instance) -> builder.add(holder, instance.getBaseValue()));
        }
        map.put(type, builder.add(attribute, value).build());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void drainSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        for (RegisterSpawnPlacementsEvent.Registration<?> reg : event.getRegistrations()) {
            net.minecraft.world.entity.SpawnPlacements.register(
                    (net.minecraft.world.entity.EntityType) reg.type(),
                    reg.placementType(), reg.heightmapType(),
                    (net.minecraft.world.entity.SpawnPlacements.SpawnPredicate) reg.predicate());
        }
    }
}
