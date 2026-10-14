package net.neoforged.neoforge.internal.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

/**
 * NeoForge's {@code ServerLifecycleHooks#runModifiers} equivalent: reads the
 * datapack-loaded {@code neoforge:biome_modifier} and
 * {@code neoforge:structure_modifier} registries and applies each modifier to
 * every biome / structure. Runs once per registry (re)build - datapack reloads
 * reconstruct the registries, so modifiers always operate on fresh vanilla
 * values and never accumulate.
 */
public final class BiomeModifierManager {
    static final Logger LOGGER = LoggerFactory.getLogger("neoforge_compat");

    private BiomeModifierManager() {
    }

    public static void applyAll(RegistryAccess registryAccess) {
        Registry<Biome> biomeRegistry = registryAccess.registry(Registries.BIOME).orElse(null);
        if (biomeRegistry == null) {
            return;
        }

        List<BiomeModifier> biomeModifiers = registryAccess
                .registry(NeoForgeRegistries.Keys.BIOME_MODIFIERS)
                .map(reg -> reg.holders().map(Holder::value).toList())
                .orElse(List.of());
        List<StructureModifier> structureModifiers = registryAccess
                .registry(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS)
                .map(reg -> reg.holders().map(Holder::value).toList())
                .orElse(List.of());

        if (biomeModifiers.isEmpty() && structureModifiers.isEmpty()) {
            return;
        }

        // Apply sorted biome modifiers to each biome (NF: biome.modifiableBiomeInfo()
        // is per-instance; ours is built from the fresh registry values here, and
        // applyBiomeModifiers writes the result onto the Biome fields).
        for (Holder<Biome> holder : biomeRegistry.holders().toList()) {
            Biome biome = holder.value();
            new ModifiableBiomeInfo(new ModifiableBiomeInfo.BiomeInfo(
                    biome.climateSettings, biome.specialEffects, biome.generationSettings, biome.mobSettings))
                    .applyBiomeModifiers(holder, biomeModifiers, registryAccess);
        }

        // NF rebuilds each chunk generator's indexed feature list after biome
        // modifiers so newly-added placed features generate.
        registryAccess.registry(Registries.LEVEL_STEM).ifPresent(stems ->
                stems.forEach(stem -> {
                    var gen = stem.generator();
                    var access = (net.neoforged.neoforge.internal.fabric.mixin.ChunkGeneratorFeatureRefreshMixin) gen;
                    access.neoforge$setFeaturesPerStep(com.google.common.base.Suppliers.memoize(() ->
                            net.neoforged.neoforge.internal.fabric.mixin.ChunkGeneratorFeatureRefreshMixin
                                    .neoforge$buildFeaturesPerStep(access.neoforge$getBiomeSource(),
                                            access.neoforge$getGenerationSettingsGetter())));
                }));

        // Apply sorted structure modifiers to each structure (apply writes the
        // modified settings onto the Structure field).
        Registry<Structure> structureRegistry = registryAccess.registry(Registries.STRUCTURE).orElse(null);
        if (structureRegistry != null) {
            for (Holder<Structure> holder : structureRegistry.holders().toList()) {
                Structure structure = holder.value();
                new ModifiableStructureInfo(new ModifiableStructureInfo.StructureInfo(structure.settings))
                        .applyStructureModifiers(holder, structureModifiers);
            }
        }

        LOGGER.debug("Applied {} biome modifiers and {} structure modifiers",
                biomeModifiers.size(), structureModifiers.size());
    }
}
