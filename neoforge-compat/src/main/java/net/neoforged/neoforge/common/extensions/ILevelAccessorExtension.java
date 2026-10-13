package net.neoforged.neoforge.common.extensions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.WorldGenRegion;

/**
 * NeoForge's {@code LevelAccessor.dimension()} patch.
 */
public interface ILevelAccessorExtension {
    private LevelAccessor self() {
        return (LevelAccessor) this;
    }

    /** Dimension of the backing level. */
    default ResourceKey<Level> dimension() {
        LevelAccessor self = self();
        if (self instanceof Level level) {
            return level.dimension();
        }
        if (self instanceof WorldGenRegion region) {
            return region.getLevel().dimension();
        }
        return Level.OVERWORLD;
    }
}
