package net.neoforged.neoforge.internal.fabric;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.datamaps.DataMapEntry;
import net.neoforged.neoforge.registries.datamaps.DataMapFile;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime store for NeoForge-style data maps. Data lives in two per-type
 * buckets: exact {@link ResourceKey} entries and {@link TagKey} entries, the
 * latter resolved lazily via {@code holder.is(tag)} so the loader never needs
 * registry access.
 */
public final class DataMapManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat_datamaps");

    private static final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> TYPES = new HashMap<>();
    private static final Map<DataMapType<?, ?>, Map<ResourceKey<?>, Object>> VALUES = new IdentityHashMap<>();
    private static final Map<DataMapType<?, ?>, List<Map.Entry<TagKey<?>, Object>>> TAG_VALUES = new IdentityHashMap<>();

    private DataMapManager() {}

    /** Called once after {@code RegisterDataMapTypesEvent} is drained. */
    public static void registerTypes(Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> types) {
        TYPES.clear();
        types.forEach((k, v) -> TYPES.put(k, new HashMap<>(v)));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <R, T> T getData(Holder<R> holder, DataMapType<R, T> type) {
        ResourceKey<R> key = holder.unwrapKey().orElse(null);
        if (key == null) {
            return null;
        }
        Map<ResourceKey<?>, Object> map = VALUES.get(type);
        if (map != null) {
            Object value = map.get(key);
            if (value != null) {
                return (T) value;
            }
        }
        List<Map.Entry<TagKey<?>, Object>> tags = TAG_VALUES.get(type);
        if (tags != null) {
            for (Map.Entry<TagKey<?>, Object> e : tags) {
                if (holder.is((TagKey<R>) e.getKey())) {
                    return (T) e.getValue();
                }
            }
        }
        return null;
    }

    /** Full wipe + load — used by the server datapack reload listener. */
    public static void loadFromResources(net.minecraft.server.packs.resources.ResourceManager manager) {
        Map<DataMapType<?, ?>, Map<ResourceKey<?>, Object>> newValues = new IdentityHashMap<>();
        Map<DataMapType<?, ?>, List<Map.Entry<TagKey<?>, Object>>> newTagValues = new IdentityHashMap<>();

        manager.listResources("data_maps", rl -> rl.getPath().endsWith(".json"))
                .forEach((location, resource) -> {
                    ParsedId parsed = parseId(location);
                    if (parsed == null) return;
                    DataMapType<Object, Object> type = findType(parsed);
                    if (type == null) {
                        LOGGER.warn("No data map type {} for file {}", parsed.typeId(), location);
                        return;
                    }
                    try (BufferedReader reader = resource.openAsReader()) {
                        JsonElement json = JsonParser.parseReader(reader);
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        com.mojang.serialization.Codec<DataMapFile<Object, Object>> codec =
                                (com.mojang.serialization.Codec) DataMapFile.codec(
                                        (ResourceKey) parsed.registryKey(), (DataMapType) type);
                        DataResult<DataMapFile<Object, Object>> result = codec.parse(JsonOps.INSTANCE, json);
                        DataMapFile<Object, Object> file = result.resultOrPartial(LOGGER::warn).orElse(null);
                        if (file == null) return;
                        if (file.replace()) {
                            newValues.remove(type);
                            newTagValues.remove(type);
                        }
                        Map<ResourceKey<?>, Object> exact = newValues.computeIfAbsent(type, k -> new HashMap<>());
                        List<Map.Entry<TagKey<?>, Object>> tagList = newTagValues.computeIfAbsent(type, k -> new ArrayList<>());
                        file.values().forEach((either, conditional) -> {
                            if (conditional.isEmpty()) return;
                            WithConditions<DataMapEntry<Object>> with = conditional.get();
                            if (!with.conditions().stream().allMatch(c -> c.test(ICondition.IContext.EMPTY))) return;
                            DataMapEntry<Object> entry = with.carrier();
                            either.ifLeft(tag -> tagList.add(Map.entry(tag, entry.value())))
                                    .ifRight(key -> exact.put(key, entry.value()));
                        });
                        file.removals().forEach(removal -> removal.key()
                                .ifLeft(tag -> tagList.removeIf(e -> e.getKey().equals(tag)))
                                .ifRight(exact::remove));
                    } catch (Exception e) {
                        LOGGER.warn("Failed to load data map {}", location, e);
                    }
                });

        VALUES.clear();
        VALUES.putAll(newValues);
        TAG_VALUES.clear();
        TAG_VALUES.putAll(newTagValues);
        int total = newValues.values().stream().mapToInt(Map::size).sum()
                + newTagValues.values().stream().mapToInt(List::size).sum();
        if (total > 0) {
            LOGGER.info("Loaded {} data map values across {} types", total,
                    newValues.size() + newTagValues.size());
        }
    }

    /** Serialize the synced snapshot to one JSON document. */
    public static byte[] encodeSyncJson() {
        com.google.gson.JsonObject root = new com.google.gson.JsonObject();
        encodeSync().forEach((typeId, entries) -> {
            com.google.gson.JsonObject o = new com.google.gson.JsonObject();
            entries.forEach(o::add);
            root.add(typeId, o);
        });
        return root.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /** Replace store with a synced snapshot (client side). */
    public static void applySyncJson(String json) {
        Map<String, Map<String, JsonElement>> snapshot = new HashMap<>();
        JsonParser.parseString(json).getAsJsonObject().entrySet().forEach(e -> {
            Map<String, JsonElement> inner = new HashMap<>();
            e.getValue().getAsJsonObject().entrySet().forEach(ie -> inner.put(ie.getKey(), ie.getValue()));
            snapshot.put(e.getKey(), inner);
        });
        applySync(snapshot);
    }

    @SuppressWarnings("unchecked")
    private static void applySync(Map<String, Map<String, JsonElement>> snapshot) {
        VALUES.clear();
        TAG_VALUES.clear();
        snapshot.forEach((typeId, entries) -> {
            DataMapType<Object, Object> type = findTypeById(ResourceLocation.tryParse(typeId));
            if (type == null || type.networkCodec() == null) return;
            entries.forEach((keyStr, valueJson) -> {
                DataResult<Object> result = type.networkCodec().parse(JsonOps.INSTANCE, valueJson);
                Object value = result.resultOrPartial(LOGGER::warn).orElse(null);
                if (value == null) return;
                if (keyStr.startsWith("#")) {
                    TagKey<Object> tag = TagKey.create(type.registryKey(),
                            ResourceLocation.tryParse(keyStr.substring(1)));
                    TAG_VALUES.computeIfAbsent(type, k -> new ArrayList<>()).add(Map.entry(tag, value));
                } else {
                    ResourceKey<Object> key = ResourceKey.create(type.registryKey(),
                            ResourceLocation.tryParse(keyStr));
                    VALUES.computeIfAbsent(type, k -> new HashMap<>()).put(key, value);
                }
            });
        });
    }

    /** Snapshot all synced types for transmission to a client. */
    public static Map<String, Map<String, JsonElement>> encodeSync() {
        Map<String, Map<String, JsonElement>> out = new HashMap<>();
        TYPES.values().forEach(map -> map.forEach((id, type) -> {
            if (type.networkCodec() == null) return;
            Map<String, JsonElement> entries = new HashMap<>();
            Map<ResourceKey<?>, Object> exact = VALUES.get(type);
            if (exact != null) {
                exact.forEach((key, value) -> encodeValue(type, value)
                        .ifPresent(json -> entries.put(key.location().toString(), json)));
            }
            List<Map.Entry<TagKey<?>, Object>> tags = TAG_VALUES.get(type);
            if (tags != null) {
                for (Map.Entry<TagKey<?>, Object> e : tags) {
                    encodeValue(type, e.getValue())
                            .ifPresent(json -> entries.put("#" + e.getKey().location(), json));
                }
            }
            if (!entries.isEmpty()) {
                out.put(id.toString(), entries);
            }
        }));
        return out;
    }

    @SuppressWarnings("unchecked")
    private static <R, T> Optional<JsonElement> encodeValue(DataMapType<R, T> type, Object value) {
        return type.networkCodec().encodeStart(JsonOps.INSTANCE, (T) value)
                .resultOrPartial(LOGGER::warn);
    }

    private static final Map<ResourceLocation, ResourceKey<Registry<?>>> REGISTRY_NAME_CACHE = new HashMap<>();

    @Nullable
    private static ParsedId parseId(ResourceLocation fileLocation) {
        // path inside data_maps/: <registry...>/<type path>.json — registry is
        // matched against known type registry keys, longest segment prefix wins.
        String full = fileLocation.getPath().substring("data_maps/".length());
        full = full.substring(0, full.length() - ".json".length());
        String[] parts = full.split("/");
        for (int split = parts.length - 1; split >= 1; split--) {
            ResourceLocation registryLoc = split == 1
                    ? ResourceLocation.withDefaultNamespace(parts[0])
                    : ResourceLocation.fromNamespaceAndPath(parts[0], String.join("/", java.util.Arrays.copyOfRange(parts, 1, split)));
            ResourceKey<Registry<?>> registryKey = findRegistryKey(registryLoc);
            if (registryKey != null) {
                String typePath = String.join("/", java.util.Arrays.copyOfRange(parts, split, parts.length));
                return new ParsedId(registryKey, ResourceLocation.fromNamespaceAndPath(fileLocation.getNamespace(), typePath));
            }
        }
        return null;
    }

    @Nullable
    private static ResourceKey<Registry<?>> findRegistryKey(ResourceLocation loc) {
        return REGISTRY_NAME_CACHE.computeIfAbsent(loc, l ->
                TYPES.keySet().stream().filter(k -> k.location().equals(l)).findFirst().orElse(null));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static DataMapType<Object, Object> findType(ParsedId parsed) {
        Map<ResourceLocation, DataMapType<?, ?>> map = TYPES.get(parsed.registryKey());
        return map == null ? null : (DataMapType<Object, Object>) map.get(parsed.typeId());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static DataMapType<Object, Object> findTypeById(ResourceLocation id) {
        for (Map<ResourceLocation, DataMapType<?, ?>> map : TYPES.values()) {
            DataMapType<?, ?> t = map.get(id);
            if (t != null) return (DataMapType<Object, Object>) t;
        }
        return null;
    }

    private record ParsedId(ResourceKey<Registry<?>> registryKey, ResourceLocation typeId) {}
}
