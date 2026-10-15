package net.alshanex.alshanex_familiars.block.entity;

import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DruidStatueEntity extends FamiliarStatueEntity {
   public DruidStatueEntity(BlockPos pPos, BlockState pBlockState) {
      super(BlockEntityRegistry.DRUID_STATUE.get(), pPos, pBlockState);
   }
}
