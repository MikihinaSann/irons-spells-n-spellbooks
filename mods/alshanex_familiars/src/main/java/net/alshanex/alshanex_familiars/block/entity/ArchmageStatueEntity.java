package net.alshanex.alshanex_familiars.block.entity;

import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ArchmageStatueEntity extends FamiliarStatueEntity {
   public ArchmageStatueEntity(BlockPos pPos, BlockState pBlockState) {
      super(BlockEntityRegistry.ARCHMAGE_STATUE.get(), pPos, pBlockState);
   }
}
