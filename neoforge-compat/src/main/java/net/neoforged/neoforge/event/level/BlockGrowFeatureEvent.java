package net.neoforged.neoforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge's BlockGrowFeatureEvent — fired before a sapling/crop grows a
 * configured feature; listeners may swap the feature or cancel growth.
 */
public class BlockGrowFeatureEvent extends Event implements ICancellableEvent {
    private final LevelAccessor level;
    private final RandomSource randomSource;
    private final BlockPos pos;
    private Holder<ConfiguredFeature<?, ?>> feature;

    public BlockGrowFeatureEvent(LevelAccessor level, RandomSource randomSource, BlockPos pos,
            Holder<ConfiguredFeature<?, ?>> feature) {
        this.level = level;
        this.randomSource = randomSource;
        this.pos = pos;
        this.feature = feature;
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public RandomSource getRandomSource() {
        return randomSource;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Holder<ConfiguredFeature<?, ?>> getFeature() {
        return feature;
    }

    public void setFeature(Holder<ConfiguredFeature<?, ?>> feature) {
        this.feature = feature;
    }
}
