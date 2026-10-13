package net.veroxuniverse.crystal_chronicles.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;

public class VeinBlock extends PipeBlock {
   public static final MapCodec<VeinBlock> CODEC = simpleCodec(VeinBlock::new);

   @Override
   public MapCodec<VeinBlock> codec() {
      return CODEC;
   }

   public VeinBlock(Properties properties) {
      super(0.3125F, properties);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(UP, Boolean.valueOf(false))
            .setValue(DOWN, Boolean.valueOf(false))
      );
   }

   private static boolean connectsTo(BlockState state) {
      return state.is((Block)CCBlocks.ARTREE_BASE.get()) || state.is((Block)CCBlocks.ARTREE_VEIN.get()) || state.is((Block)CCBlocks.ARTREE_CAPILLARY.get());
   }

   private static boolean shouldHaveRandomUp(BlockPos pos) {
      long hash = pos.asLong();
      hash ^= hash >> 33;
      hash *= -49064778989728563L;
      hash ^= hash >> 33;
      return (hash & 15L) == 0L;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return getStateWithConnections(ctx.getLevel(), ctx.getClickedPos(), this.defaultBlockState());
   }

   public static BlockState getStateWithConnections(BlockGetter level, BlockPos pos, BlockState baseState) {
      BlockState below = level.getBlockState(pos.below());
      BlockState above = level.getBlockState(pos.above());
      BlockState north = level.getBlockState(pos.north());
      BlockState east = level.getBlockState(pos.east());
      BlockState south = level.getBlockState(pos.south());
      BlockState west = level.getBlockState(pos.west());
      boolean connectDown = connectsTo(below);
      boolean connectNorth = connectsTo(north);
      boolean connectEast = connectsTo(east);
      boolean connectSouth = connectsTo(south);
      boolean connectWest = connectsTo(west);
      boolean connectUpToTree = connectsTo(above);
      boolean randomUpStub = above.isAir() && !connectUpToTree && shouldHaveRandomUp(pos);
      boolean connectUp = connectUpToTree || randomUpStub;
      return baseState.setValue(DOWN, Boolean.valueOf(connectDown))
         .setValue(UP, Boolean.valueOf(connectUp))
         .setValue(NORTH, Boolean.valueOf(connectNorth))
         .setValue(EAST, Boolean.valueOf(connectEast))
         .setValue(SOUTH, Boolean.valueOf(connectSouth))
         .setValue(WEST, Boolean.valueOf(connectWest));
   }

   @Override
   protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if (!state.canSurvive(level, currentPos)) {
         level.scheduleTick(currentPos, this, 1);
         return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
      } else {
         return getStateWithConnections(level, currentPos, state);
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   @Override
   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
      super.onPlace(state, level, pos, oldState, isMoving);
      if (!level.isClientSide) {
         BlockState fixed = getStateWithConnections(level, pos, state);
         if (fixed != state) {
            level.setBlock(pos, fixed, 2);
         }
      }
   }
}
