package net.neoforged.neoforgespi.locating;

import java.nio.file.Path;

/**
 * Minimal stand-in for NeoForge's {@code IModFile} — resolves paths relative to a mod's root.
 */
public interface IModFile {
    Path findResource(String... pathName);

    /** File name of the mod jar/dir — used by mod-list generators. */
    default String getFileName() {
        Path p = findResource();
        return p != null && p.getFileName() != null ? p.getFileName().toString() : "unknown";
    }
}
