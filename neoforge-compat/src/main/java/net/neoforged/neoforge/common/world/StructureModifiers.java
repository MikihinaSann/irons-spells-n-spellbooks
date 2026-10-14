package net.neoforged.neoforge.common.world;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.NeoForgeMod;

/**
 * The stock {@link StructureModifier} implementations bundled with NeoForge.
 * Ported from NeoForge 21.1.
 */
public final class StructureModifiers {
    private StructureModifiers() {} // Utility class.

    /**
     * A structure modifier that adds spawn entries to structures.
     */
    public record AddSpawnsStructureModifier(HolderSet<Structure> structures,
            List<SpawnerData> spawners) implements StructureModifier {
        /**
         * Convenience factory for a single spawn.
         */
        public static AddSpawnsStructureModifier singleSpawn(HolderSet<Structure> structures, SpawnerData spawner) {
            return new AddSpawnsStructureModifier(structures, List.of(spawner));
        }

        @Override
        public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
            if (phase == Phase.ADD && this.structures.contains(structure)) {
                StructureSettingsBuilder settingsBuilder = builder.getStructureSettings();
                for (SpawnerData spawner : this.spawners) {
                    EntityType<?> type = spawner.type;
                    settingsBuilder.getOrAddSpawnOverrides(type.getCategory()).addSpawn(spawner);
                }
            }
        }

        @Override
        public MapCodec<? extends StructureModifier> codec() {
            return NeoForgeMod.ADD_SPAWNS_STRUCTURE_MODIFIER_TYPE.get();
        }
    }

    /**
     * A structure modifier that removes all spawns of given entity types from structures.
     */
    public record RemoveSpawnsStructureModifier(HolderSet<Structure> structures,
            HolderSet<EntityType<?>> entityTypes) implements StructureModifier {
        @Override
        public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
            if (phase == Phase.REMOVE && this.structures.contains(structure)) {
                StructureSettingsBuilder settingsBuilder = builder.getStructureSettings();
                for (MobCategory category : MobCategory.values()) {
                    var overrides = settingsBuilder.getSpawnOverrides(category);
                    if (overrides == null || overrides.getSpawns().isEmpty())
                        continue;
                    overrides.removeSpawns(spawnerData -> {
                        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(spawnerData.type);
                        return this.entityTypes.contains(BuiltInRegistries.ENTITY_TYPE.getHolderOrThrow(ResourceKey.create(Registries.ENTITY_TYPE, key)));
                    });
                }
            }
        }

        @Override
        public MapCodec<? extends StructureModifier> codec() {
            return NeoForgeMod.REMOVE_SPAWNS_STRUCTURE_MODIFIER_TYPE.get();
        }
    }

    /**
     * A structure modifier that removes all spawns of given mob categories from structures.
     */
    public record ClearSpawnsStructureModifier(HolderSet<Structure> structures,
            Set<MobCategory> categories) implements StructureModifier {
        @Override
        public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
            if (phase == Phase.REMOVE && this.structures.contains(structure)) {
                StructureSettingsBuilder settingsBuilder = builder.getStructureSettings();
                for (MobCategory category : this.categories) {
                    settingsBuilder.removeSpawnOverrides(category);
                }
            }
        }

        @Override
        public MapCodec<? extends StructureModifier> codec() {
            return NeoForgeMod.CLEAR_SPAWNS_STRUCTURE_MODIFIER_TYPE.get();
        }
    }
}
