package net.hazen.hazennstuff.Item.Block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;

public class HnSFlammableRotatedPillarBlock extends RotatedPillarBlock {
   public HnSFlammableRotatedPillarBlock(Properties properties) {
      super(properties);
   }

   public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return true;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 5;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 5;
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      if (context.getItemInHand().getItem() instanceof AxeItem) {
         if (state.is(HnSBlocks.WISEWOOD_LOG)) {
            return (BlockState)((Block)HnSBlocks.STRIPPED_WISEWOOD_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is(HnSBlocks.WISEWOOD_WOOD)) {
            return (BlockState)((Block)HnSBlocks.STRIPPED_WISEWOOD_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is(HnSBlocks.FROSTBITE_BIRCH_LOG)) {
            return (BlockState)((Block)HnSBlocks.STRIPPED_FROSTBITE_BIRCH_LOG.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }

         if (state.is(HnSBlocks.FROSTBITE_BIRCH_WOOD)) {
            return (BlockState)((Block)HnSBlocks.STRIPPED_FROSTBITE_BIRCH_WOOD.get()).defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS));
         }
      }

      return super.getToolModifiedState(state, context, itemAbility, simulate);
   }
}
