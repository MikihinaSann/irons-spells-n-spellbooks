package net.neoforged.fml;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * ModContainer stub — {@link #registerConfig} routes to the simple config loader.
 */
public class ModContainer {
    private final net.fabricmc.loader.api.ModContainer fabric;
    private final String modIdOverride;

    public ModContainer(net.fabricmc.loader.api.ModContainer fabric) {
        this(fabric, null);
    }

    /**
     * @param configNamePrefix distinct file prefix for merged-in addons, so
     *                         their default config file names don't collide
     *                         with the host mod's (e.g. irons_spellbooks-client.toml).
     *                         The config is still registered under the real
     *                         (loaded) fabric mod id — FCAP validates it.
     */
    public ModContainer(net.fabricmc.loader.api.ModContainer fabric, String configNamePrefix) {
        this.fabric = fabric;
        this.modIdOverride = configNamePrefix;
    }

    public ModContainer() {
        this(null, null);
    }

    public net.neoforged.neoforgespi.language.IModInfo getModInfo() {
        return fabric != null ? new net.neoforged.neoforgespi.language.IModInfo.FabricModInfo(fabric)
                : new net.neoforged.neoforgespi.language.IModInfo() {
                    @Override
                    public String getModId() {
                        return "ironsspellbooks";
                    }

                    @Override
                    public ModVersion getVersion() {
                        return new ModVersion() {
                            @Override
                            public String toString() {
                                return "0.0.0";
                            }
                        };
                    }
                };
    }

    public String getModId() {
        return fabric != null ? fabric.getMetadata().getId() : "irons_spellbooks";
    }

    public void registerConfig(ModConfig.Type type, ModConfigSpec spec, String fileName) {
        net.neoforged.neoforge.internal.fabric.config.FabricConfigLoader.register(getModId(), type, spec, fileName);
    }

    public void registerConfig(ModConfig.Type type, ModConfigSpec spec) {
        String prefix = modIdOverride != null ? modIdOverride : getModId();
        registerConfig(type, spec, prefix + "-" + type.extension() + ".toml");
    }
}
