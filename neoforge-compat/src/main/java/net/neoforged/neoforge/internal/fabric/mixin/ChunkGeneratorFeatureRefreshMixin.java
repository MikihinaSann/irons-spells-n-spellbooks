package net.neoforged.neoforge.internal.fabric.mixin;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * Accessors for {@link ChunkGenerator}'s indexed feature list — NeoForge
 * exposes {@code refreshFeaturesPerStep()} so biome modifiers applied after
 * registry load are visible to chunk generation. The step-data element type is
 * package-private in vanilla, hence the wildcard signatures.
 */
@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorFeatureRefreshMixin {

    @Accessor("featuresPerStep")
    @Mutable
    void neoforge$setFeaturesPerStep(Supplier<List<?>> supplier);

    @Accessor("biomeSource")
    BiomeSource neoforge$getBiomeSource();

    @Accessor("generationSettingsGetter")
    Function<Holder<Biome>, BiomeGenerationSettings> neoforge$getGenerationSettingsGetter();

    @Invoker("method_44215")
    static List<?> neoforge$buildFeaturesPerStep(
            BiomeSource biomeSource, Function<Holder<Biome>, BiomeGenerationSettings> settingsGetter) {
        throw new AssertionError();
    }
}
