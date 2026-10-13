package net.neoforged.neoforge.common.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Built-in biome modifier implementation types (data-driven).
 */
public class BiomeModifiers {

    /** Adds features to biomes at a decoration step. */
    public record AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features,
                                           GenerationStep.Decoration step) implements BiomeModifier {
        public static final MapCodec<AddFeaturesBiomeModifier> CODEC = MapCodec.unit(
                () -> new AddFeaturesBiomeModifier(HolderSet.empty(), HolderSet.empty(),
                        GenerationStep.Decoration.TOP_LAYER_MODIFICATION));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }
}
