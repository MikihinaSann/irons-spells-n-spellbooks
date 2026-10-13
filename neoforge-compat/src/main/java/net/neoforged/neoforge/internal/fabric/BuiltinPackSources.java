package net.neoforged.neoforge.internal.fabric;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Collects {@link RepositorySource}s contributed through the NeoForge-style
 * {@code AddPackFindersEvent}. The {@code PackRepository} ctor mixin appends
 * them so built-in packs (ISS legacy pack, merged-addon packs) show up in the
 * pack screen exactly as they did on NeoForge.
 */
public final class BuiltinPackSources {
    private static final Map<PackType, List<RepositorySource>> COLLECTED = new EnumMap<>(PackType.class);

    private BuiltinPackSources() {
    }

    public static void addAll(PackType type, List<RepositorySource> sources) {
        COLLECTED.computeIfAbsent(type, t -> new ArrayList<>()).addAll(sources);
    }

    /** All collected sources regardless of pack type — each pack self-declares its type. */
    public static List<RepositorySource> all() {
        List<RepositorySource> all = new ArrayList<>();
        COLLECTED.values().forEach(all::addAll);
        return all;
    }
}
