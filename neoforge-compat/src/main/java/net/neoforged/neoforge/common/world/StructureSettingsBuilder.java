package net.neoforged.neoforge.common.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.jetbrains.annotations.Nullable;

/**
 * A mutable builder for {@link StructureSettings}, used by {@link StructureModifier}s.
 * Ported from NeoForge 21.1.
 */
public class StructureSettingsBuilder {
    private HolderSet<Biome> biomes;
    private final Map<MobCategory, StructureSpawnOverrideBuilder> spawnOverrides;
    private GenerationStep.Decoration step;
    private TerrainAdjustment terrainAdaptation;

    /**
     * @return a builder seeded with a mutable copy of the given settings
     */
    public static StructureSettingsBuilder copyOf(StructureSettings settings) {
        return new StructureSettingsBuilder(settings.biomes(), settings.spawnOverrides(), settings.step(), settings.terrainAdaptation());
    }

    private StructureSettingsBuilder(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
        this.biomes = biomes;
        this.spawnOverrides = spawnOverrides.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> StructureSpawnOverrideBuilder.copyOf(entry.getValue())));
        this.step = step;
        this.terrainAdaptation = terrainAdaptation;
    }

    /**
     * @return a new immutable {@link StructureSettings} reflecting the builder state
     */
    public StructureSettings build() {
        Map<MobCategory, StructureSpawnOverride> overrides = Collections.unmodifiableMap(
                this.spawnOverrides.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().build())));
        return new StructureSettings(this.biomes, overrides, this.step, this.terrainAdaptation);
    }

    public HolderSet<Biome> getBiomes() {
        return biomes;
    }

    public void setBiomes(HolderSet<Biome> biomes) {
        this.biomes = biomes;
    }

    /**
     * @return the spawn override builder for the given category, or null if none exists
     */
    @Nullable
    public StructureSpawnOverrideBuilder getSpawnOverrides(MobCategory category) {
        return spawnOverrides.get(category);
    }

    /**
     * @return the spawn override builder for the given category, creating it if absent
     */
    public StructureSpawnOverrideBuilder getOrAddSpawnOverrides(MobCategory category) {
        return spawnOverrides.computeIfAbsent(category, c -> new StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType.PIECE, Collections.emptyList()));
    }

    /**
     * Removes the spawn overrides for the given category entirely.
     */
    public void removeSpawnOverrides(MobCategory category) {
        this.spawnOverrides.remove(category);
    }

    /**
     * @return the decoration step the structure generates in
     */
    public GenerationStep.Decoration getDecorationStep() {
        return step;
    }

    /**
     * Sets the decoration step the structure generates in.
     */
    public void setDecorationStep(GenerationStep.Decoration step) {
        this.step = step;
    }

    /**
     * @return the terrain adaptation used by the structure
     */
    public TerrainAdjustment getTerrainAdaptation() {
        return terrainAdaptation;
    }

    /**
     * Sets the terrain adaptation used by the structure.
     */
    public void setTerrainAdaptation(TerrainAdjustment terrainAdaptation) {
        this.terrainAdaptation = terrainAdaptation;
    }

    public static class StructureSpawnOverrideBuilder {
        private StructureSpawnOverride.BoundingBoxType boundingBox;
        private final List<MobSpawnSettings.SpawnerData> spawns;
        private final List<MobSpawnSettings.SpawnerData> spawnsView;

        /**
         * @return a builder seeded with a mutable copy of the given override
         */
        public static StructureSpawnOverrideBuilder copyOf(StructureSpawnOverride override) {
            return new StructureSpawnOverrideBuilder(override.boundingBox(), override.spawns().unwrap());
        }

        private StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType boundingBox, List<MobSpawnSettings.SpawnerData> spawns) {
            this.boundingBox = boundingBox;
            this.spawns = new ArrayList<>(spawns);
            this.spawnsView = Collections.unmodifiableList(this.spawns);
        }

        /**
         * @return the bounding box type for these spawn overrides
         */
        public StructureSpawnOverride.BoundingBoxType getBoundingBox() {
            return boundingBox;
        }

        /**
         * Sets the bounding box type for these spawn overrides.
         */
        public void setBoundingBox(StructureSpawnOverride.BoundingBoxType boundingBox) {
            this.boundingBox = boundingBox;
        }

        /**
         * @return an immutable view of the configured spawns
         */
        public List<MobSpawnSettings.SpawnerData> getSpawns() {
            return spawnsView;
        }

        /**
         * Adds a spawn entry to this override.
         */
        public void addSpawn(MobSpawnSettings.SpawnerData spawn) {
            this.spawns.add(spawn);
        }

        /**
         * Removes a specific spawn entry.
         */
        public void removeSpawn(MobSpawnSettings.SpawnerData spawn) {
            this.spawns.remove(spawn);
        }

        /**
         * Removes all spawn entries matching the given predicate.
         */
        public void removeSpawns(Predicate<MobSpawnSettings.SpawnerData> spawnPredicate) {
            this.spawns.removeIf(spawnPredicate);
        }

        /**
         * @return a new {@link StructureSpawnOverride} reflecting the builder state
         */
        public StructureSpawnOverride build() {
            return new StructureSpawnOverride(boundingBox, WeightedRandomList.create(spawns));
        }
    }
}
