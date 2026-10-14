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
