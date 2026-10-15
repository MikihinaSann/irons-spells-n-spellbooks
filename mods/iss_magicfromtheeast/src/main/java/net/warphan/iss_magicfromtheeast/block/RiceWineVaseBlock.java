package net.warphan.iss_magicfromtheeast.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RiceWineVaseBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<RiceWineVaseBlock> CODEC = simpleCodec(p -> new RiceWineVaseBlock());
   public static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);

   public MapCodec<RiceWineVaseBlock> codec() {
      return CODEC;
   }

   public RiceWineVaseBlock() {
      super(
         Properties.of()
            .mapColor(DyeColor.RED)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
            .sound(
               new SoundType(
                  1.0F,
                  1.0F,
                  SoundEvents.DECORATED_POT_PLACE,
                  SoundEvents.MUD_BRICKS_STEP,
                  SoundEvents.DECORATED_POT_PLACE,
                  SoundEvents.DECORATED_POT_HIT,
                  SoundEvents.DECORATED_POT_BREAK
               )
            )
            .strength(0.2F)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return canSupportCenter(pLevel, pPos.below(), Direction.UP);
   }

   protected BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos
   ) {
      if ((Boolean)pState.getValue(BlockStateProperties.WATERLOGGED)) {
         pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      Direction direction = pContext.getHorizontalDirection();
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, direction.getOpposite()))
         .setValue(BlockStateProperties.WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER);
   }

   protected FluidState getFluidState(BlockState pState) {
      return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{BlockStateProperties.WATERLOGGED});
      pBuilder.add(new Property[]{FACING});
   }
}
