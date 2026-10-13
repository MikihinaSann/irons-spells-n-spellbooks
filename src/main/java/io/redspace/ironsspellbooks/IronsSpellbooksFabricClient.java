package io.redspace.ironsspellbooks;

import io.redspace.ironsspellbooks.platform.fabric.FabricEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
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
 * Fabric client entrypoint. Posts the NeoForge-style client registration
 * events onto the mod bus, drains them into Fabric/vanilla registries, and
 * wires the runtime dispatch bridges for client gameplay events.
 */
public class IronsSpellbooksFabricClient implements ClientModInitializer {
    private static final FabricEventBus MOD_BUS = IronsSpellbooksFabric.MOD_BUS;

    private static void sub(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            MOD_BUS.register(clazz);
            NeoForge.EVENT_BUS.register(clazz);
        } catch (Throwable t) {
            IronsSpellbooks.LOGGER.error("Failed to register client subscriber {}", className, t);
        }
    }

    @Override
    public void onInitializeClient() {
        sub("io.redspace.ironsspellbooks.api.util.BossbarManager");
        sub("io.redspace.ironsspellbooks.gui.overlays.RecastOverlay");
        sub("io.redspace.ironsspellbooks.particle.ClientShieldHelper");
        sub("io.redspace.ironsspellbooks.particle.FallingBlockParticle");
        sub("io.redspace.ironsspellbooks.player.ClientInputEvents");
        sub("io.redspace.ironsspellbooks.player.ClientPlayerEvents");
        sub("io.redspace.ironsspellbooks.player.KeyMappings");
        sub("io.redspace.ironsspellbooks.registries.OverlayRegistry");
        sub("io.redspace.ironsspellbooks.setup.ClientSetup");
        sub("io.redspace.ironslib.internal.client.ClientEvents");
        sub("io.redspace.ironslib.internal.client.ClientInputEvents");
        sub("io.redspace.ironslib.internal.client.ClientSetup");
        sub("com.grandsigil.client.GsClient$Events");
        sub("com.grandsigil.client.GsClient$Setup");
        sub("com.grandsigil.client.ShaderProbe");
        sub("net.veroxuniverse.crystal_chronicles.CrystalChronicles$ClientModEvents");
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("tacz")) {
            sub("com.mumu17.ironsarms.client.ChargeManaToAmmoBoxTick");
        }
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("epicfight")) {
            sub("com.yukami.efiscompat.effect.RenderSiphonRay");
            sub("com.yukami.efiscompat.effect.ArrowChargeAttachmentRenderer");
        }

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

        // NeoForge consumes IClientItemExtensions.getCustomRenderer through the
        // item renderer's builtin/entity path; on Fabric the equivalent hook is
        // BuiltinItemRendererRegistry. Renderers are created lazily — most need
        // Minecraft's ItemRenderer/entity models — and memoized per extension.
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

        // NeoForge's pack-finder event for client resources — collected sources are
        // appended to the vanilla PackRepository by PackRepositoryMixin.
        net.neoforged.neoforge.event.AddPackFindersEvent clientPacks =
                new net.neoforged.neoforge.event.AddPackFindersEvent(net.minecraft.server.packs.PackType.CLIENT_RESOURCES);
        MOD_BUS.post(clientPacks);
        io.redspace.ironsspellbooks.platform.fabric.BuiltinPackSources.addAll(
                net.minecraft.server.packs.PackType.CLIENT_RESOURCES, clientPacks.getSources());

        RegisterDimensionSpecialEffectsEvent effects = new RegisterDimensionSpecialEffectsEvent();
        MOD_BUS.post(effects);
        try {
            net.minecraft.client.renderer.DimensionSpecialEffects.EFFECTS.putAll(
                    RegisterDimensionSpecialEffectsEvent.EFFECTS);
        } catch (Throwable t) {
            IronsSpellbooks.LOGGER.error("Failed to inject dimension special effects", t);
        }

        // FAPI's per-dimension sky renderer is the hook for
        // IDimensionSpecialEffectsExtension.renderSky — fires at the HEAD of
        // LevelRenderer.renderSky and skips vanilla sky when a renderer exists.
        // isFoggy/setupFog aren't exposed by WorldRenderContext, but
        // PocketDimensionEffects.renderSky ignores both params.
        net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.registerSkyRenderer(
                io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager.POCKET_DIMENSION, context -> {
                    if (context.world().effects() instanceof io.redspace.ironsspellbooks.render.PocketDimensionEffects pocketEffects) {
                        pocketEffects.renderSky(context.world(),
                                (int) context.world().getGameTime(),
                                context.tickCounter().getGameTimeDeltaPartialTick(true),
                                context.positionMatrix(), context.camera(), context.projectionMatrix(),
                                false, () -> {
                                });
                    }
                });

        RegisterGuiLayersEvent guiLayers = new RegisterGuiLayersEvent();
        MOD_BUS.post(guiLayers);
        // Registrations are bucketed by their vanilla anchor; GuiLayerOrderMixin
        // renders each slot inside the vanilla HUD chain. Anything unmapped falls
        // back to HudRenderCallback here (after the whole HUD).
        guiLayers.getRegistrations().forEach(io.redspace.ironsspellbooks.platform.fabric.GuiLayerSlots::classify);
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            io.redspace.ironsspellbooks.platform.fabric.GuiLayerSlots.render(
                    io.redspace.ironsspellbooks.platform.fabric.GuiLayerSlots.Slot.FALLBACK, guiGraphics, deltaTracker);
            NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.RenderGuiEvent.Post(
                    guiGraphics, deltaTracker));
        });

        net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.EVENT.register(context -> {
            try {
                MOD_BUS.post(new net.neoforged.neoforge.client.event.RegisterShadersEvent(context));
            } catch (Exception e) {
                IronsSpellbooks.LOGGER.error("Shader registration failed", e);
            }
        });

        ModelEvent.RegisterAdditional additional = new ModelEvent.RegisterAdditional();
        MOD_BUS.post(additional);
        ModelLoadingPlugin.register(context ->
                additional.getModels().forEach(model -> context.addModels(model.id())));
        net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin.register(
                io.redspace.ironsspellbooks.platform.fabric.IssModelLoadingPlugin::scanSeparateTransforms,
                new io.redspace.ironsspellbooks.platform.fabric.IssModelLoadingPlugin());

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
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                NeoForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingOut(
                        client.gameMode, client.player, handler)));
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) ->
                NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, world)));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) ->
                NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity, world)));
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) ->
                NeoForge.EVENT_BUS.post(new ItemTooltipEvent(stack, Minecraft.getInstance().player, lines, type)));
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

        // Dev-only autojoin: -Diss.autojoin=host:port connects once the title screen appears.
        String autojoin = System.getProperty("iss.autojoin");
        if (autojoin != null && !autojoin.isEmpty()) {
            final String[] lastScreen = {""};
            final long[] lastConnectAttempt = {0L};
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                String name = client.screen == null ? "<null>" : client.screen.getClass().getName();
                if (!name.equals(lastScreen[0])) {
                    lastScreen[0] = name;
                    IronsSpellbooks.LOGGER.info("ISS-AUTOJOIN screen={} title={}", name,
                            client.screen == null ? "-" : client.screen.getTitle().getString());
                }
                if (client.screen instanceof net.minecraft.client.gui.screens.DeathScreen
                        && client.player != null) {
                    client.player.respawn();
                }
                if (client.screen instanceof net.minecraft.client.gui.screens.TitleScreen
                        || client.screen instanceof net.minecraft.client.gui.screens.DisconnectedScreen) {
                    if (System.currentTimeMillis() - lastConnectAttempt[0] < 5000) {
                        return;
                    }
                    lastConnectAttempt[0] = System.currentTimeMillis();
                    net.minecraft.client.multiplayer.ServerData data = new net.minecraft.client.multiplayer.ServerData(
                            "dev-autojoin", autojoin, net.minecraft.client.multiplayer.ServerData.Type.OTHER);
                    net.minecraft.client.gui.screens.ConnectScreen.startConnecting(
                            client.screen, client,
                            net.minecraft.client.multiplayer.resolver.ServerAddress.parseString(autojoin),
                            data, false, null);
                }
            });
        }
    }
}
