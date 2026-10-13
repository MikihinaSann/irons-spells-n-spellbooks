package io.redspace.ironsspellbooks;

import io.redspace.ironsspellbooks.platform.fabric.FabricEventBus;
import io.redspace.ironslib.IronsLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifiers;
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
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;

/**
 * Fabric entrypoint standing in for NeoForge's mod-lifecycle dispatch.
 * Constructs the mod, registers {@code @EventBusSubscriber} classes onto the
 * two buses, then fires the lifecycle events in NeoForge's order and drains the
 * collected registrations into Fabric/vanilla registries.
 */
public class IronsSpellbooksFabric implements ModInitializer {
    public static final FabricEventBus MOD_BUS = new FabricEventBus();
    private static ModContainer container;

    public static ModContainer getContainer() {
        return container;
    }

    /** Mirrors {@code @EventBusSubscriber}: no bus => registered on both buses. */
    private static void sub(String className, boolean clientOnly) {
        if (clientOnly) {
            return;
        }
        try {
            Class<?> clazz = Class.forName(className);
            MOD_BUS.register(clazz);
            NeoForge.EVENT_BUS.register(clazz);
        } catch (Throwable t) {
            IronsSpellbooks.LOGGER.error("Failed to register subscriber {}", className, t);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onInitialize() {
        IronsSpellbooks.LOGGER.info("ISS-FABRIC BOOT envType={}", FabricLoader.getInstance().getEnvironmentType());
        // EnumArgument's type info must be registered or command-tree sync
        // (LAN open, dedicated server join) throws "Unrecognized argument type".
        net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "enum"),
                (Class) net.neoforged.neoforge.server.command.EnumArgument.class,
                new net.neoforged.neoforge.server.command.EnumArgument.Info());
        // ---- subscriber classes (common side; client-only classes are
        // registered by the client entrypoint) ----
        sub("io.redspace.ironslib.attribute.AttributeEventsHandler", false);
        sub("io.redspace.ironslib.internal.client.ClientEvents", false);
        sub("io.redspace.ironslib.internal.client.ClientInputEvents", true);
        sub("io.redspace.ironslib.internal.client.ClientSetup", true);
        sub("io.redspace.ironslib.internal.network.PayloadHandler", false);
        sub("io.redspace.ironslib.internal.registry.CommandHandler", false);
        sub("io.redspace.ironslib.kinetic_weapon.KineticWeaponHandler", false);
        sub("io.redspace.ironslib.registry.IronsLibRegistries", false);
        sub("io.redspace.ironslib.registry.IronsLibRegistries$AttributeRegistry", false);
        sub("io.redspace.ironslib.soulfire.SoulFireHooks", false);
        sub("io.redspace.ironsspellbooks.api.config.SpellConfigManager", false);
        sub("io.redspace.ironsspellbooks.api.registry.AttributeRegistry", false);
        sub("io.redspace.ironsspellbooks.api.util.CameraShakeManager", false);
        sub("io.redspace.ironsspellbooks.api.util.FogManager", false);
        sub("io.redspace.ironsspellbooks.api.util.MusicManager", false);
        sub("io.redspace.ironsspellbooks.capabilities.magic.PortalManager", false);
        sub("io.redspace.ironsspellbooks.capabilities.magic.SummonManager", false);
        sub("io.redspace.ironsspellbooks.damage.DamageSources", false);
        sub("io.redspace.ironsspellbooks.datagen.DataGenerators", false);
        sub("io.redspace.ironsspellbooks.effect.BlightEffect", false);
        sub("io.redspace.ironsspellbooks.effect.EchoingStrikesEffect", false);
        sub("io.redspace.ironsspellbooks.effect.FrostbiteEffect", false);
        sub("io.redspace.ironsspellbooks.effect.GluttonyEffect", false);
        sub("io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager", false);
        sub("io.redspace.ironsspellbooks.effect.OakskinEffect", false);
        sub("io.redspace.ironsspellbooks.effect.SacrificialMarkEffect", false);
        sub("io.redspace.ironsspellbooks.effect.SoulBurnMobEffect", false);
        sub("io.redspace.ironsspellbooks.effect.SpiderAspectEffect", false);
        sub("io.redspace.ironsspellbooks.effect.TrueInvisibilityEffect", false);
        sub("io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss", false);
        sub("io.redspace.ironsspellbooks.entity.mobs.ice_spider.IceSpiderEntity", false);
        sub("io.redspace.ironsspellbooks.entity.mobs.wizards.WizardAIEvents", false);
        sub("io.redspace.ironsspellbooks.item.curios.BetrayerSignetRingItem", false);
        sub("io.redspace.ironsspellbooks.item.curios.ExpulsionRing", false);
        sub("io.redspace.ironsspellbooks.item.curios.LurkerRing", false);
        sub("io.redspace.ironsspellbooks.item.curios.WickedBoneRingItem", false);
        sub("io.redspace.ironsspellbooks.item.FurledMapCraftableItem", false);
        sub("io.redspace.ironsspellbooks.loot.LootDebugEvents", false);
        sub("io.redspace.ironsspellbooks.player.AdditionalWanderingTrades", false);
        sub("io.redspace.ironsspellbooks.player.ServerPlayerEvents", false);
        sub("io.redspace.ironsspellbooks.registries.CommandRegistry", false);
        sub("io.redspace.ironsspellbooks.registries.CreativeTabRegistry", false);
        sub("io.redspace.ironsspellbooks.registries.PotionRegistry", false);
        sub("io.redspace.ironsspellbooks.setup.CommonSetup", false);
        sub("io.redspace.ironsspellbooks.setup.PayloadHandler", false);
        sub("io.redspace.ironsspellbooks.worldgen.VillageAddition", false);
        sub("com.grandsigil.event.GsEvents", false);
        sub("net.veroxuniverse.crystal_chronicles.event.CCEvents$RegistrationHandler", false);

        // ---- construct the mods ----
        container = new ModContainer(FabricLoader.getInstance()
                .getModContainer(IronsSpellbooks.MODID)
                .orElseThrow());
        new IronsSpellbooks(MOD_BUS, container);
        new IronsLib(MOD_BUS, new ModContainer(FabricLoader.getInstance()
                .getModContainer(IronsLib.MODID)
                .orElseGet(() -> FabricLoader.getInstance()
                        .getModContainer(IronsSpellbooks.MODID).orElseThrow())));
        net.fabricmc.loader.api.ModContainer issFabric = FabricLoader.getInstance()
                .getModContainer(IronsSpellbooks.MODID).orElseThrow();
        new com.grandsigil.GrandSigil(MOD_BUS, new ModContainer(issFabric, "grandsigil"));
        new net.veroxuniverse.crystal_chronicles.CrystalChronicles(MOD_BUS, new ModContainer(issFabric, "crystal_chronicles"));
        if (FabricLoader.getInstance().isModLoaded("epicfight")) {
            sub("com.yukami.efiscompat.player.PlayerAnimationEvents", false);
            new com.yukami.efiscompat.EpicFightIronCompat(MOD_BUS, new ModContainer(issFabric, "efiscompat"));
        }
        if (FabricLoader.getInstance().isModLoaded("tacz")) {
            sub("com.mumu17.ironsarms.item.GunTooltipHandler", false);
            new com.mumu17.ironsarms.IronsArms(MOD_BUS, new ModContainer(issFabric, "ironsarms"));
        }

        // ---- NeoForge lifecycle order ----
        MOD_BUS.post(new NewRegistryEvent());
        MOD_BUS.post(new net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry());
        DeferredRegister.drainAll();
        net.minecraft.core.registries.BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof top.theillusivec4.curios.api.type.capability.ICurioItem curioItem) {
                dev.emi.trinkets.api.TrinketsApi.registerTrinket(item,
                        new top.theillusivec4.curios.api.type.capability.TrinketCurioAdapter(curioItem));
            }
        });
        // items are registered — default-component modifications apply now
        // (furled maps stamp their FurledMapData component here)
        MOD_BUS.post(new net.neoforged.neoforge.event.ModifyDefaultComponentsEvent());
        MOD_BUS.post(new FMLCommonSetupEvent());

        net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent payloadEvent =
                new net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent();
        MOD_BUS.post(payloadEvent);
        payloadEvent.getRegistrars().values().forEach(registrar ->
                registrar.getRegistrations().forEach(net.neoforged.neoforge.network.registration.PayloadRegistrar.Registration::register));

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

        // reload listeners -> FAPI SERVER_DATA resource-manager listeners
        AddReloadListenerEvent reloadEvent = new AddReloadListenerEvent();
        MOD_BUS.post(reloadEvent);
        NeoForge.EVENT_BUS.post(reloadEvent);
        int[] counter = {0};
        reloadEvent.getListeners().forEach(listener ->
                ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                        new net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener() {
                            private final net.minecraft.resources.ResourceLocation id =
                                    IronsSpellbooks.id("reload_listener_" + (counter[0]++));

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

        // game-bus bridges
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        NeoForge.EVENT_BUS.post(new RegisterCommandsEvent(dispatcher, registryAccess, environment)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK.register(server ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.ServerTickEvent.Pre(server != null, server)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.ServerTickEvent.Post(server != null, server)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_WORLD_TICK.register(level ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.LevelTickEvent.Pre(level)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(level ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.LevelTickEvent.Post(level)));
        net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent(
                        (net.minecraft.world.item.alchemy.PotionBrewing.Builder) builder)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
                NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, world)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) ->
                NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity, world)));
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStartingEvent(server)));
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStartedEvent(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStoppingEvent(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStoppedEvent(server)));

        // Angel Wings grants elytra flight without a chest stack — FAPI's
        // EntityElytraEvents.CUSTOM is the hook NeoForge's
        // ItemStack.canElytraFly/elytraFlightTick correspond to.
        net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents.CUSTOM.register(
                (entity, tickElytra) -> entity.hasEffect(io.redspace.ironsspellbooks.registries.MobEffectRegistry.ANGEL_WINGS));

        // --- player lifecycle + datapack sync ---
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.server.ServerAboutToStartEvent(server)));
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(handler.getPlayer()));
            // NeoForge fires OnDatapackSyncEvent per joining player; SpellConfigManager
            // builds the active spell config map and pushes SyncJsonConfigPacket here.
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.OnDatapackSyncEvent(server.getPlayerList(), handler.getPlayer()));
        });
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent(handler.getPlayer())));
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent(newPlayer, alive)));
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.COPY_FROM.register((original, clone, keepEverything) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone(clone, original, !keepEverything)));
        net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent(player, origin.dimension(), destination.dimension())));
        // global datapack sync (/reload, world load) — null player broadcasts to all
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
        // BlockEvent.BreakEvent — fabric's BEFORE_BREAK maps to NeoForge's cancellable break event
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            var event = new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(world, pos, state, player);
            NeoForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        });
        // brewing recipes — NeoForge posts during PotionBrewing bootstrap; FAPI exposes the same builder
        net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent(builder)));

        // BuildCreativeModeTabContentsEvent — NeoForge posts this once per tab
        // while the tab populates; FAPI exposes the same hook via
        // MODIFY_ENTRIES_ALL. The shim buffers additions, drained here.
        net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            var key = net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).orElse(null);
            var event = new net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent();
            event.setTab(tab);
            event.setTabKey(key);
            event.setParameters(entries.getContext());
            MOD_BUS.post(event);
            int applied = 0;
            for (var addition : event.getAdditions()) {
                if (key != null && key.equals(addition.tabKey())) {
                    addition.action().accept(new net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent.TabOutput(entries));
                    applied++;
                }
            }
            if (applied > 0 || (key != null && key.location().getNamespace().equals("irons_spellbooks"))) {
                IronsSpellbooks.LOGGER.info("ISS-TABBUILD key={} additions={} applied={}", key, event.getAdditions().size(), applied);
            }
        });

        // config lifecycle events — wired in FabricConfigLoader.register per mod id,
        // so configs loading during registration get their Loading event too.

        // builtin pack finders (server-side datapack hooks)
        AddPackFindersEvent serverPacks = new AddPackFindersEvent(PackType.SERVER_DATA);
        MOD_BUS.post(serverPacks);
        io.redspace.ironsspellbooks.platform.fabric.BuiltinPackSources.addAll(PackType.SERVER_DATA, serverPacks.getSources());

        // advancements: spellbook equip trigger over Trinkets
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.TRIGGER_TYPES,
                IronsSpellbooks.id("equip_spellbook"), io.redspace.ironsspellbooks.advancements.EquipSpellbookTrigger.INSTANCE);
        dev.emi.trinkets.api.event.TrinketEquipCallback.EVENT.register((stack, ref, entity) -> {
            if (entity instanceof net.minecraft.server.level.ServerPlayer player
                    && ref.inventory().getSlotType().getName().equals(io.redspace.ironsspellbooks.compat.Curios.SPELLBOOK_SLOT)) {
                io.redspace.ironsspellbooks.advancements.EquipSpellbookTrigger.INSTANCE.trigger(player);
            }
        });

        // biome modifiers (ported from data/.../neoforge/biome_modifier JSONs)
        var overworld = net.fabricmc.fabric.api.biome.v1.BiomeSelectors.tag(net.minecraft.tags.BiomeTags.IS_OVERWORLD);
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addFeature(overworld,
                net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_ORES,
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.PLACED_FEATURE,
                        IronsSpellbooks.id("ore_mithril_placement")));
        net.fabricmc.fabric.api.biome.v1.BiomeModifications.addSpawn(overworld,
                net.minecraft.world.entity.MobCategory.MONSTER,
                io.redspace.ironsspellbooks.registries.EntityRegistry.NECROMANCER.get(), 18, 1, 1);

        // global loot modifiers: neoforge:loot_table_id condition + JSON loader
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.LOOT_CONDITION_TYPE,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "loot_table_id"),
                net.neoforged.neoforge.common.conditions.LootTableIdCondition.TYPE);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                new net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener() {
                    @Override
                    public net.minecraft.resources.ResourceLocation getFabricId() {
                        return IronsSpellbooks.id("global_loot_modifiers");
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
                                        io.redspace.ironsspellbooks.platform.fabric.GlobalLootModifierManager.collect(manager),
                                        backgroundExecutor)
                                .thenCompose(barrier::wait);
                    }
                });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) ->
                io.redspace.ironsspellbooks.platform.fabric.GlobalLootModifierManager.decode(server));
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                io.redspace.ironsspellbooks.platform.fabric.GlobalLootModifierManager.decode(server));
    }

    /** Merge an additional attribute into a vanilla default supplier map. */
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
