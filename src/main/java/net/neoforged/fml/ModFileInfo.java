package net.neoforged.fml;

import java.nio.file.Path;

/**
 * Minimal stand-in exposing a mod's root path for builtin pack discovery.
 */
public class ModFileInfo {
    private final Path root;

    public ModFileInfo(Path root) {
        this.root = root;
    }

    public ModFile getFile() {
        return new ModFile(root);
    }

    public static class ModFile implements net.neoforged.neoforgespi.locating.IModFile {
        private final Path root;

        public ModFile(Path root) {
            this.root = root;
        }

        @Override
        public java.nio.file.Path findResource(String... path) {
            if (root == null) {
                return null;
            }
            return root.resolve(String.join("/", path));
        }
    }
}
