package net.neoforged.fml.loading;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.function.Supplier;

public enum FMLPaths {
    MODSDIR,
    CONFIGDIR,
    GAMEDIR;

    private Path path;

    public Path get() {
        if (path == null) {
            path = switch (this) {
                case MODSDIR -> FabricLoader.getInstance().getGameDir().resolve("mods");
                case CONFIGDIR -> FabricLoader.getInstance().getConfigDir();
                case GAMEDIR -> FabricLoader.getInstance().getGameDir();
            };
        }
        return path;
    }

    public Path relative() {
        return FabricLoader.getInstance().getGameDir().relativize(get());
    }

    public static Path getOrCreateGameRelativePath(Path path) {
        return getOrCreateGameRelativePath(path, "");
    }

    public static Path getOrCreateGameRelativePath(Path path, String name) {
        return resolveAndCreate(path, name);
    }

    private static Path resolveAndCreate(Path path, String name) {
        try {
            java.nio.file.Files.createDirectories(path);
        } catch (java.io.IOException ignored) {
        }
        return name.isEmpty() ? path : path.resolve(name);
    }
}
