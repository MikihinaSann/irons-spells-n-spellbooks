package net.neoforged.neoforge.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/** NeoForge IPlantable stub - plantable items/blocks implement this. */
public interface IPlantable {
    default PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
    }

    default BlockState getPlant(BlockGetter level, BlockPos pos) {
        return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }

    enum PlantType {
        PLAINS, DESERT, BEACH, CAVE, WATER, NETHER, CROP
    }
}
