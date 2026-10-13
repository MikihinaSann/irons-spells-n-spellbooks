package net.neoforged.neoforge.common.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * NeoForge's biome modifier contract. Datapack instances live under
 * {@code data/<ns>/neoforge/biome_modifier/*.json} and are translated into
 * Fabric {@code BiomeModifications} by the platform bridge.
 */
public interface BiomeModifier {

    void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder);

    MapCodec<? extends BiomeModifier> codec();

    enum Phase {
        BEFORE_EVERYTHING, ADD, REMOVE, MODIFY, AFTER_EVERYTHING
    }
}
