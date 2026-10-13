package io.redspace.ironsspellbooks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * ISS-side client setup hook, invoked by the neoforge_compat lifecycle driver
 * (via the {@code neoforge:client_setup} entrypoint) after the generic client
 * lifecycle has run. Holds ISS-specific client registrations: the pocket
 * dimension sky renderer and the dev autojoin helper.
 */
public class IronsSpellbooksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        IronsSpellbooks.LOGGER.info("ISS-FABRIC CLIENT SETUP");

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
