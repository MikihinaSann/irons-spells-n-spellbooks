package net.veroxuniverse.crystal_chronicles.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CCSaplingBlock extends SaplingBlock {
   private Block block;

   public CCSaplingBlock(TreeGrower treeGrower, Properties properties, Block block) {
      super(treeGrower, properties);
      this.block = block;
   }

   @Override
   protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return pState.is(this.block);
   }
}
