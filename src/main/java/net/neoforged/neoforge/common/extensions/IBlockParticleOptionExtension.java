package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

/**
 * NeoForge adds a block-position override to {@code BlockParticleOption} (used
 * by falling-block/tremor particles). Stored in a side map on Fabric.
 */
public interface IBlockParticleOptionExtension {
    java.util.Map<BlockParticleOption, BlockPos> POSITIONS = new java.util.WeakHashMap<>();

    private BlockParticleOption self() {
        return (BlockParticleOption) (Object) this;
    }

    default BlockParticleOption setPos(BlockPos pos) {
        POSITIONS.put(self(), pos);
        return self();
    }

    default BlockPos getPos() {
        return POSITIONS.getOrDefault(self(), BlockPos.ZERO);
    }
}
