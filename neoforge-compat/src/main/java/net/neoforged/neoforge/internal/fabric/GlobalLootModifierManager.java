package net.neoforged.neoforge.internal.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fabric replacement for NeoForge's global loot modifier pipeline: collects
 * {@code data/<ns>/loot_modifiers/} JSONs on server-data reload, decodes them
 * with the server's full registry access once datapack registries exist, and
 * applies them inside {@code LootTable#getRandomItems(LootContext)}.
 */
public final class GlobalLootModifierManager {
    static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");
    private static volatile List<IGlobalLootModifier> modifiers = List.of();
    private static final List<JsonObject> PENDING = new ArrayList<>();
    private static final ThreadLocal<ResourceLocation> CURRENT_TABLE = new ThreadLocal<>();

    private static MinecraftServer lastServer;
    private static IdentityHashMap<LootTable, ResourceLocation> tableIds = new IdentityHashMap<>();

    private GlobalLootModifierManager() {
    }

    public static ResourceLocation currentTableId() {
        return CURRENT_TABLE.get();
    }

    /** Entry point called by the LootTable mixin after vanilla generation. */
    public static ObjectArrayList<ItemStack> apply(LootTable table, ObjectArrayList<ItemStack> loot, LootContext context) {
        List<IGlobalLootModifier> active = modifiers;
        if (active.isEmpty()) {
            return loot;
        }
        try {
            CURRENT_TABLE.set(tableId(table, context));
            for (IGlobalLootModifier modifier : active) {
                loot = modifier.apply(loot, context);
            }
        } catch (Throwable t) {
            LOGGER.error("Global loot modifier failed on {}", CURRENT_TABLE.get(), t);
        } finally {
            CURRENT_TABLE.remove();
        }
        return loot;
    }

    private static ResourceLocation tableId(LootTable table, LootContext context) {
        MinecraftServer server = context.getLevel().getServer();
        if (server != lastServer) {
            lastServer = server;
            IdentityHashMap<LootTable, ResourceLocation> map = new IdentityHashMap<>();
            var registries = server.reloadableRegistries();
            for (ResourceLocation id : registries.getKeys(net.minecraft.core.registries.Registries.LOOT_TABLE)) {
                map.put(registries.getLootTable(net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.LOOT_TABLE, id)), id);
            }
            tableIds = map;
        }
        return tableIds.get(table);
    }

    /** Reload listener body — caches every {@code loot_modifiers/*.json}. */
    public static void collect(ResourceManager manager) {
        PENDING.clear();
        for (Map.Entry<ResourceLocation, net.minecraft.server.packs.resources.Resource> entry :
                manager.listResources("loot_modifiers", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8))) {
                JsonElement json = com.google.gson.JsonParser.parseReader(reader);
                if (json.isJsonObject() && json.getAsJsonObject().has("type")) {
                    PENDING.add(json.getAsJsonObject());
                }
            } catch (Throwable t) {
                LOGGER.warn("Failed to read loot modifier {}", entry.getKey(), t);
            }
        }
    }

    /**
     * Decodes cached JSONs against the server's full registry access. Called
     * from {@code END_DATA_PACK_RELOAD} / {@code SERVER_STARTED} once the
     * datapack registries (predicate, loot tables, ...) are populated.
     */
    public static void decode(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }
        List<JsonObject> batch;
        synchronized (PENDING) {
            batch = List.copyOf(PENDING);
            PENDING.clear();
        }
        List<IGlobalLootModifier> parsed = new ArrayList<>();
        RegistryOps<JsonElement> ops = RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE,
                server.reloadableRegistries().get());
        Registry<MapCodec<? extends IGlobalLootModifier>> serializers = NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS;
        for (JsonObject obj : batch) {
            try {
                ResourceLocation type = ResourceLocation.parse(obj.get("type").getAsString());
                MapCodec<? extends IGlobalLootModifier> codec = serializers.get(type);
                if (codec == null) {
                    LOGGER.warn("Unknown global loot modifier type {}", type);
                    continue;
                }
                codec.codec().decode(ops, obj).resultOrPartial(
                                msg -> LOGGER.warn("Failed to parse loot modifier: {}", msg))
                        .ifPresent(pair -> parsed.add(pair.getFirst()));
            } catch (Throwable t) {
                LOGGER.warn("Failed to decode loot modifier {}", obj, t);
            }
        }
        modifiers = List.copyOf(parsed);
        lastServer = null;
        LOGGER.info("Loaded {} global loot modifiers", modifiers.size());
    }
}
