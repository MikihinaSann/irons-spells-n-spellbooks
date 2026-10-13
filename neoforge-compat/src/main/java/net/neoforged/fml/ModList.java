package net.neoforged.fml;

import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.Optional;

/**
 * ModList backed by {@link FabricLoader}.
 */
public class ModList {
    private static final ModList INSTANCE = new ModList();

    public static ModList get() {
        return INSTANCE;
    }

    public boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public Optional<ModContainer> getModContainerById(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).map(ModContainer::new);
    }

    public ModFileInfo getModFileById(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(c -> new ModFileInfo(c.getRootPaths()))
                .orElse(null);
    }

    public int size() {
        return FabricLoader.getInstance().getAllMods().size();
    }

    public java.util.stream.Stream<IModInfo> mods() {
        return FabricLoader.getInstance().getAllMods().stream().map(IModInfo.FabricModInfo::new);
    }

    public java.util.List<IModInfo> getMods() {
        return mods().toList();
    }

    /** Fabric has no scan-data system — return empty (annotation scanning unused at runtime). */
    public java.util.List<net.neoforged.neoforgespi.language.ModFileScanData> getAllScanData() {
        return java.util.List.of();
    }
}
