package net.neoforged.neoforge.internal.fabric;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.ClientExtensionsRegistry;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Map;

/**
 * The client-side NeoForge lifecycle: registration events on the shared mod
 * bus drained into Fabric/vanilla registries, plus the runtime event bridges
 * that map Fabric callbacks onto NeoForge game-bus events.
 */
final class NeoForgeCompatLifecycle {

    private NeoForgeCompatLifecycle() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void runClientLifecycle() {
        var MOD_BUS = NeoForgeCompat.MOD_BUS;

        MOD_BUS.post(new FMLClientSetupEvent());

        RegisterKeyMappingsEvent keyMappings = new RegisterKeyMappingsEvent();
        MOD_BUS.post(keyMappings);
        keyMappings.drain();

        EntityRenderersEvent.RegisterRenderers renderers = new EntityRenderersEvent.RegisterRenderers();
        MOD_BUS.post(renderers);
        renderers.drain();

        EntityRenderersEvent.RegisterLayerDefinitions layers = new EntityRenderersEvent.RegisterLayerDefinitions();
        MOD_BUS.post(layers);
        layers.getLayers().forEach((modelLocation, supplier) ->
                EntityModelLayerRegistry.registerModelLayer(modelLocation, supplier::get));

        RegisterColorHandlersEvent.Block blockColors = new RegisterColorHandlersEvent.Block();
        MOD_BUS.post(blockColors);
        blockColors.drain();
        RegisterColorHandlersEvent.Item itemColors = new RegisterColorHandlersEvent.Item();
        MOD_BUS.post(itemColors);
        itemColors.drain();

        RegisterMenuScreensEvent menus = new RegisterMenuScreensEvent();
        MOD_BUS.post(menus);
        menus.drain();

        RegisterParticleProvidersEvent particles = new RegisterParticleProvidersEvent();
        MOD_BUS.post(particles);
        particles.drain();

        MOD_BUS.post(new RegisterClientExtensionsEvent());

        MOD_BUS.post(new net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent());

        // NeoForge calls Item#initializeClient / FluidType#initializeClient for every
        // registered entry during client init - the extension lands in
        // ClientExtensionsRegistry via the consumer.
        net.minecraft.core.registries.BuiltInRegistries.ITEM.forEach(item ->
                item.initializeClient(ext -> ClientExtensionsRegistry.registerItem(item, ext)));
        net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES.forEach(type ->
                type.initializeClient(ext -> ClientExtensionsRegistry.registerFluidType(type, ext)));
        NeoForgeFluidRenderHandlers.registerAll();

        net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent clientReload =
                new net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent();
        MOD_BUS.post(clientReload);
        clientReload.getListeners().forEach(listener ->
                net.fabricmc.fabric.api.resource.ResourceManagerHelper
                        .get(net.minecraft.server.packs.PackType.CLIENT_RESOURCES)
                        .registerReloadListener(new net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener() {
                            private final net.minecraft.resources.ResourceLocation id =
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                            "neoforge", "client_reload_listener_" + listener.getName().hashCode());

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
                        }));

        // NeoForge registers client commands against CommandSourceStack and merges
        // the root nodes into the vanilla dispatcher; mirror that.
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> nfDispatcher =
                    new com.mojang.brigadier.CommandDispatcher<>();
            NeoForge.EVENT_BUS.post(
                    new net.neoforged.neoforge.client.event.RegisterClientCommandsEvent(nfDispatcher, buildContext));
            for (var node : nfDispatcher.getRoot().getChildren()) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                var raw = (com.mojang.brigadier.tree.CommandNode) node;
                dispatcher.getRoot().addChild(raw);
            }
        });

        // NeoForge consumes IClientItemExtensions.getCustomRenderer through the
        // item renderer's builtin/entity path; on Fabric the equivalent hook is
        // BuiltinItemRendererRegistry. Renderers are created lazily and memoized.
        Map<IClientItemExtensions, net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer> rendererCache = new java.util.HashMap<>();
        ClientExtensionsRegistry.getItemExtensions().forEach((item, ext) -> {
            try {
                if (ext.getClass().getMethod("getCustomRenderer").getDeclaringClass() == IClientItemExtensions.class) {
                    return;
                }
            } catch (NoSuchMethodException e) {
                return;
            }
            net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(item,
                    (stack, mode, poseStack, buffers, light, overlay) -> {
                        var renderer = rendererCache.computeIfAbsent(ext, IClientItemExtensions::getCustomRenderer);
                        if (renderer != null) {
                            renderer.renderByItem(stack, mode, poseStack, buffers, light, overlay);
                        }
                    });
        });

        net.neoforged.neoforge.event.AddPackFindersEvent clientPacks =
                new net.neoforged.neoforge.event.AddPackFindersEvent(net.minecraft.server.packs.PackType.CLIENT_RESOURCES);
        MOD_BUS.post(clientPacks);
        BuiltinPackSources.addAll(
                net.minecraft.server.packs.PackType.CLIENT_RESOURCES, clientPacks.getSources());

        RegisterDimensionSpecialEffectsEvent effects = new RegisterDimensionSpecialEffectsEvent();
        MOD_BUS.post(effects);
        try {
            net.minecraft.client.renderer.DimensionSpecialEffects.EFFECTS.putAll(
                    RegisterDimensionSpecialEffectsEvent.EFFECTS);
        } catch (Throwable t) {
            NeoForgeCompat.LOGGER.error("Failed to inject dimension special effects", t);
        }

        RegisterGuiLayersEvent guiLayers = new RegisterGuiLayersEvent();
        MOD_BUS.post(guiLayers);
        guiLayers.getRegistrations().forEach(GuiLayerSlots::classify);
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            GuiLayerSlots.render(GuiLayerSlots.Slot.FALLBACK, guiGraphics, deltaTracker);
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.RenderGuiEvent.Post(
                    guiGraphics, deltaTracker));
        });

        CoreShaderRegistrationCallback.EVENT.register(context -> {
            try {
                MOD_BUS.post(new net.neoforged.neoforge.client.event.RegisterShadersEvent(context));
            } catch (Exception e) {
                NeoForgeCompat.LOGGER.error("Shader registration failed", e);
            }
        });

        ModelEvent.RegisterAdditional additional = new ModelEvent.RegisterAdditional();
        MOD_BUS.post(additional);
        ModelLoadingPlugin.register(context ->
                additional.getModels().forEach(model -> context.addModels(model.id())));
        net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin.register(
                net.neoforged.neoforge.internal.fabric.NeoForgeModelLoadingPlugin::scanResources,
                new net.neoforged.neoforge.internal.fabric.NeoForgeModelLoadingPlugin());

        // ---- runtime dispatch bridges ----
        ClientTickEvents.START_CLIENT_TICK.register(client ->
                NeoForge.EVENT_BUS.post(new ClientTickEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(client ->
                NeoForge.EVENT_BUS.post(new ClientTickEvent.Post()));
        ClientTickEvents.START_WORLD_TICK.register(level ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.LevelTickEvent.Pre(level)));
        ClientTickEvents.END_WORLD_TICK.register(level ->
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.LevelTickEvent.Post(level)));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                NeoForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingIn(
                        client.gameMode, client.player, handler)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.level != null) {
                NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.LevelEvent.Unload(client.level));
            }
            NeoForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingOut(
                    client.gameMode, client.player, handler));
        });
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            entity.onAddedToLevel();
            NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, world));
        });
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            entity.onRemovedFromLevel();
            NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity, world));
        });
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            NeoForge.EVENT_BUS.post(new ItemTooltipEvent(stack, Minecraft.getInstance().player, lines, type));
            // Curios parity: ICurioItem.getAttributesTooltip appends description
            // lines and "When equipped as <slot>:" headers to the tooltip.
            if (stack.getItem() instanceof top.theillusivec4.curios.api.type.capability.ICurioItem curioItem) {
                curioItem.getAttributesTooltip(lines, context, stack);
            }
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) ->
                NeoForge.EVENT_BUS.post(new ClientChatReceivedEvent(message, true)));
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) ->
                NeoForge.EVENT_BUS.post(new ClientChatReceivedEvent.Player(message, sender != null ? sender.getId() : null, message)));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                NeoForge.EVENT_BUS.post(new ScreenEvent.Opening(client.screen, screen)));
        WorldRenderEvents.AFTER_ENTITIES.register(context ->
                NeoForge.EVENT_BUS.post(new RenderLevelStageEvent(
                        RenderLevelStageEvent.Stage.AFTER_ENTITIES,
                        context.worldRenderer(), context.matrixStack(),
                        context.matrixStack().last().pose(), context.projectionMatrix(),
                        Minecraft.getInstance().level != null ? (int) Minecraft.getInstance().level.getGameTime() : 0,
                        context.tickCounter(), context.camera(), context.frustum())));
    }
}
