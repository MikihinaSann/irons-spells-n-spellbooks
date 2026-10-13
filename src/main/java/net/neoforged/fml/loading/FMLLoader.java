package net.neoforged.fml.loading;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.api.distmarker.Dist;

public class FMLLoader {
    public static Dist getDist() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Dist.CLIENT : Dist.DEDICATED_SERVER;
    }

    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /** NeoForge-style access; backed by the Fabric {@link FabricLoader} container list. */
    public static net.neoforged.fml.ModList getLoadingModList() {
        return net.neoforged.fml.ModList.get();
    }
}
