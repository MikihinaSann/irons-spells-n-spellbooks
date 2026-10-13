package net.neoforged.fml;

import java.nio.file.Path;

/**
 * Minimal stand-in exposing a mod's root path for builtin pack discovery.
 */
public class ModFileInfo {
    private final java.util.List<Path> roots;

    public ModFileInfo(java.util.List<Path> roots) {
        this.roots = roots == null ? java.util.List.of() : roots;
    }

    public ModFileInfo(Path root) {
        this.roots = root == null ? java.util.List.of() : java.util.List.of(root);
    }

    public ModFile getFile() {
        return new ModFile(roots);
    }

    public static class ModFile implements net.neoforged.neoforgespi.locating.IModFile {
        private final java.util.List<Path> roots;

        public ModFile(java.util.List<Path> roots) {
            this.roots = roots == null ? java.util.List.of() : roots;
        }

        @Override
        public java.nio.file.Path findResource(String... path) {
            // resolve the first root that actually contains the resource; fall
            // back to the first root so callers still get a usable path
            for (Path root : roots) {
                Path candidate = root.resolve(String.join("/", path));
                if (java.nio.file.Files.exists(candidate)) {
                    return candidate;
                }
            }
            return roots.isEmpty() ? null : roots.get(0).resolve(String.join("/", path));
        }
    }
}
