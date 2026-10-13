package net.neoforged.neoforge.internal.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge-style biome modifier pipeline: loads
 * {@code data/<ns>/neoforge/biome_modifier/*.json}, decodes each through the
 * {@code neoforge:biome_modifier_serializers} registry, then applies every
 * modifier to every biome each time registries (re)load - mirroring NeoForge's
 * datapack-driven biome modification.
 */
public final class BiomeModifierManager {
    static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");

    private static final java.util.Map<ResourceLocation, JsonElement> RAW = new java.util.LinkedHashMap<>();

    private BiomeModifierManager() {
    }

    /** Collects raw modifier JSONs during a datapack reload. */
    public static void collect(ResourceManager manager) {
        RAW.clear();
        for (var entry : manager.listResources("neoforge/biome_modifier",
                p -> p.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                RAW.put(entry.getKey(), JsonParser.parseReader(reader));
            } catch (Exception e) {
                LOGGER.warn("Failed to read biome modifier {}", entry.getKey(), e);
            }
        }
    }

    /**
     * Decodes the collected modifier JSONs and applies every modifier to every
     * biome in the (re)built registry — call once per datapack/registry load.
     */
    public static void applyAll(RegistryAccess registryAccess) {
        if (RAW.isEmpty()) {
            return;
        }
        Registry<Biome> biomeRegistry = registryAccess.registry(Registries.BIOME).orElse(null);
        if (biomeRegistry == null) {
            return;
        }
        java.util.function.Function<BiomeModifier, com.mojang.serialization.MapCodec<? extends BiomeModifier>> typeFn =
                BiomeModifier::codec;
        // registry values are MapCodecs; dispatch consumes them directly
        java.util.function.Function<com.mojang.serialization.MapCodec<? extends BiomeModifier>, com.mojang.serialization.MapCodec<? extends BiomeModifier>> decoderFn =
                c -> c;
        @SuppressWarnings({"unchecked", "rawtypes"})
        Codec<BiomeModifier> dispatchCodec = (Codec<BiomeModifier>) ((Codec) NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS.byNameCodec())
                .dispatch("type", typeFn, decoderFn);
        DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        List<BiomeModifier> modifiers = new ArrayList<>();
        for (var entry : RAW.entrySet()) {
            dispatchCodec.decode(ops, entry.getValue()).resultOrPartial(err ->
                    LOGGER.warn("Failed to decode biome modifier {}: {}", entry.getKey(), err))
                    .ifPresent(pair -> modifiers.add(pair.getFirst()));
        }
        if (modifiers.isEmpty()) {
            return;
        }
        for (Holder<Biome> holder : biomeRegistry.holders().toList()) {
            Biome biome = holder.value();
            ModifiableBiomeInfo info = new ModifiableBiomeInfo(new ModifiableBiomeInfo.BiomeInfo(
                    biome.climateSettings, biome.specialEffects, biome.generationSettings, biome.mobSettings));
            if (info.applyBiomeModifiers(holder, modifiers, registryAccess)) {
                ModifiableBiomeInfo.BiomeInfo modified = info.getModifiedBiomeInfo();
                biome.generationSettings = modified.generationSettings();
                biome.mobSettings = modified.mobSpawnSettings();
                biome.specialEffects = modified.effects();
                biome.climateSettings = modified.climateSettings();
            }
        }
    }
}
