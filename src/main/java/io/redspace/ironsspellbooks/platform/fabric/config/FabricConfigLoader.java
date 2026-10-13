package io.redspace.ironsspellbooks.platform.fabric.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import io.redspace.ironsspellbooks.IronsSpellbooksFabric;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers {@link ModConfigSpec} instances with ForgeConfigAPIPort, which
 * provides the real NeoForge config implementation (TOML load/save, file
 * watching, reloads), and bridges FCAP's load/reload/unload callbacks onto the
 * NeoForge-style mod bus as {@link ModConfigEvent}s.
 */
public final class FabricConfigLoader {
    private static final Map<String, ModConfig> CONFIGS = new ConcurrentHashMap<>();
    /** Mod ids whose FCAP config events are already bridged to the mod bus. */
    private static final Set<String> WIRED_MOD_IDS = ConcurrentHashMap.newKeySet();
    /** Configs a {@link ModConfigEvent.Loading} was already posted for. */
    private static final Set<ModConfig> LOADING_POSTED = ConcurrentHashMap.newKeySet();

    private FabricConfigLoader() {
    }

    public static void register(String modId, ModConfig.Type type, ModConfigSpec spec, String fileName) {
        try {
            wireConfigEvents(modId);
            ModConfig config = NeoForgeConfigRegistry.INSTANCE.register(modId, type, spec, fileName);
            CONFIGS.put(fileName, config);
            // FCAP loads common/client configs synchronously inside register(),
            // before the config enters CONFIGS, so the bridge listener always
            // misses that first load; post the Loading event now instead.
            if (config.getLoadedConfig() != null && LOADING_POSTED.add(config)) {
                IronsSpellbooksFabric.MOD_BUS.post(new ModConfigEvent.Loading(config));
            }
        } catch (Throwable t) {
            io.redspace.ironsspellbooks.IronsSpellbooks.LOGGER.error("Failed to register config {}", fileName, t);
        }
    }

    /**
     * Attaches FCAP's per-mod config callbacks once per mod id. Events are only
     * reposted for configs that came through {@link #register} — the same mod id
     * can own configs registered by other mods, and subscribers filter by spec.
     */
    private static void wireConfigEvents(String modId) {
        if (!WIRED_MOD_IDS.add(modId)) {
            return;
        }
        NeoForgeModConfigEvents.loading(modId).register(config -> {
            if (CONFIGS.containsValue(config)) {
                LOADING_POSTED.add(config);
                IronsSpellbooksFabric.MOD_BUS.post(new ModConfigEvent.Loading(config));
            }
        });
        NeoForgeModConfigEvents.reloading(modId).register(config -> {
            if (CONFIGS.containsValue(config)) {
                IronsSpellbooksFabric.MOD_BUS.post(new ModConfigEvent.Reloading(config));
            }
        });
        NeoForgeModConfigEvents.unloading(modId).register(config -> {
            if (CONFIGS.containsValue(config)) {
                IronsSpellbooksFabric.MOD_BUS.post(new ModConfigEvent.Unloading(config));
            }
        });
    }

    public static ModConfig get(String fileName) {
        return CONFIGS.get(fileName);
    }
}
