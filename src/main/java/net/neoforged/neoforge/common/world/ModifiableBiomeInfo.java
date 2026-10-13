package net.neoforged.neoforge.common.world;

import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

/**
 * Mutable view of a biome's info, used by {@link BiomeModifier}s.
 */
public record ModifiableBiomeInfo(
        BiomeInfo original,
        BiomeInfo modified) {

    public record BiomeInfo(
            BiomeGenerationSettings generationSettings,
            MobSpawnSettings mobSpawnSettings,
            BiomeSpecialEffects specialEffects) {

        public static BiomeInfo of(net.minecraft.world.level.biome.Biome biome) {
            return new BiomeInfo(
                    biome.getGenerationSettings(),
                    biome.getMobSettings(),
                    biome.getSpecialEffects());
        }

        /**
         * Builder accumulating modifications applied back onto the biome.
         */
        public static class Builder {
            private final BiomeInfo base;
            private BiomeGenerationSettings generationSettings;
            private MobSpawnSettings mobSpawnSettings;
            private BiomeSpecialEffects specialEffects;

            public Builder(BiomeInfo base) {
                this.base = base;
                this.generationSettings = base.generationSettings;
                this.mobSpawnSettings = base.mobSpawnSettings;
                this.specialEffects = base.specialEffects;
            }

            public BiomeGenerationSettings getGenerationSettings() {
                return generationSettings;
            }

            public MobSpawnSettings getMobSpawnSettings() {
                return mobSpawnSettings;
            }

            public BiomeSpecialEffects getSpecialEffects() {
                return specialEffects;
            }

            public BiomeInfo build() {
                return new BiomeInfo(generationSettings, mobSpawnSettings, specialEffects);
            }
        }
    }
}
