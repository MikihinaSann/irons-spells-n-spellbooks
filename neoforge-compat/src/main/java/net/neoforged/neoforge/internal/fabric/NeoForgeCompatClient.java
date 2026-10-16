package net.neoforged.neoforge.internal.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Client half of the NeoForge-compat lifecycle driver: scans client
 * subscribers, constructs {@code "neoforge:client"} @Mod classes, runs the
 * client lifecycle, then invokes {@code "neoforge:client_setup"} hooks.
 */
public class NeoForgeCompatClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");

    @Override
    public void onInitializeClient() {
        // IEntityWithComplexSpawn: the server sends SpawnDataPayload right after
        // the vanilla add-entity packet; apply readSpawnData on the game thread.
        // (playS2C codec already registered by the common onInitialize)
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
                SpawnDataPayload.TYPE, (payload, context) -> context.client().execute(() -> {
                    var level = net.minecraft.client.Minecraft.getInstance().level;
                    if (level == null) return;
                    var entity = level.getEntity(payload.entityId());
                    if (entity instanceof net.neoforged.neoforge.entity.IEntityWithComplexSpawn complex) {
                        net.minecraft.network.RegistryFriendlyByteBuf buf = new net.minecraft.network.RegistryFriendlyByteBuf(
                                io.netty.buffer.Unpooled.wrappedBuffer(payload.data()), level.registryAccess());
                        complex.readSpawnData(buf);
                        buf.release();
                    }
                }));

        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
                DataMapsSyncPayload.TYPE, (payload, context) -> context.client().execute(() -> {
                    net.neoforged.neoforge.internal.fabric.DataMapManager.applySyncJson(
                            new String(payload.json(), java.nio.charset.StandardCharsets.UTF_8));
                    var level = net.minecraft.client.Minecraft.getInstance().level;
                    if (level != null) {
                        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(
                                new net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent(
                                        level.registryAccess(), net.minecraft.core.registries.BuiltInRegistries.ITEM,
                                        net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent.UpdateCause.CLIENT_SYNC));
                    }
                }));

        // The common onInitialize already scanned subscribers on this JVM
        // (deduped); a dedicated-server JVM skipped client-only ones correctly.
        net.neoforged.neoforge.common.util.LogicalSidedProvider.setClient(
                net.minecraft.client.Minecraft::getInstance);
        List<net.fabricmc.loader.api.ModContainer> mods = NeoForgeCompat.discoverNeoForgeMods();
        NeoForgeCompat.constructMods(mods, "neoforge:client");
        NeoForgeCompatLifecycle.runClientLifecycle();
        NeoForgeCompat.runHooks("neoforge:client_setup");
        LOGGER.info("neoforge_compat client lifecycle complete");
    }
}
