package net.alshanex.alshanex_familiars.block.entity;

import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ClericStatueEntity extends FamiliarStatueEntity {
   public ClericStatueEntity(BlockPos pPos, BlockState pBlockState) {
      super(BlockEntityRegistry.CLERIC_STATUE.get(), pPos, pBlockState);
   }
}
