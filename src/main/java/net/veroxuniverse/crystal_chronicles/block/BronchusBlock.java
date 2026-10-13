package net.veroxuniverse.crystal_chronicles.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BronchusBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   public BronchusBlock(Properties pProperties) {
      super(pProperties);
   }

   @Override
   protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Direction direction = pState.getValue(FACING);
      switch (direction) {
         case NORTH:
         default:
            return this.makeShapeNorthSouth();
         case SOUTH:
            return this.makeShapeNorthSouth();
         case EAST:
         case WEST:
            return this.makeShapeEastWest();
         case DOWN:
         case UP:
            return this.makeShapeUpDown();
      }
   }

   public VoxelShape makeShapeUpDown() {
      VoxelShape shape1 = Shapes.empty();
      shape1 = Shapes.join(shape1, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.125), BooleanOp.OR);
      shape1 = Shapes.join(shape1, Shapes.box(0.0, 0.0, 0.875, 1.0, 1.0, 1.0), BooleanOp.OR);
      shape1 = Shapes.join(shape1, Shapes.box(0.875, 0.0, 0.125, 1.0, 1.0, 0.875), BooleanOp.OR);
      return Shapes.join(shape1, Shapes.box(0.0, 0.0, 0.125, 0.125, 1.0, 0.875), BooleanOp.OR);
   }

   public VoxelShape makeShapeEastWest() {
      VoxelShape shape2 = Shapes.empty();
      shape2 = Shapes.join(shape2, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.125), BooleanOp.OR);
      shape2 = Shapes.join(shape2, Shapes.box(0.0, 0.0, 0.875, 1.0, 1.0, 1.0), BooleanOp.OR);
      shape2 = Shapes.join(shape2, Shapes.box(0.0, 0.875, 0.125, 1.0, 1.0, 0.875), BooleanOp.OR);
      return Shapes.join(shape2, Shapes.box(0.0, 0.0, 0.125, 1.0, 0.125, 0.875), BooleanOp.OR);
   }

   public VoxelShape makeShapeNorthSouth() {
      VoxelShape shape3 = Shapes.empty();
      shape3 = Shapes.join(shape3, Shapes.box(0.0, 0.0, 0.0, 0.125, 1.0, 1.0), BooleanOp.OR);
      shape3 = Shapes.join(shape3, Shapes.box(0.875, 0.0, 0.0, 1.0, 1.0, 1.0), BooleanOp.OR);
      shape3 = Shapes.join(shape3, Shapes.box(0.125, 0.875, 0.0, 0.875, 1.0, 1.0), BooleanOp.OR);
      return Shapes.join(shape3, Shapes.box(0.125, 0.0, 0.0, 0.875, 0.125, 1.0), BooleanOp.OR);
   }

   @Override
   protected BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos
   ) {
      return pDirection == pState.getValue(FACING).getOpposite() && !pState.canSurvive(pLevel, pPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
   }

   @Override
   protected BlockState rotate(BlockState pState, Rotation pRotation) {
      return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
   }

   @Override
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @NotNull
   @Override
   public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      Direction direction = pState.getValue(FACING);
      switch (direction) {
         case NORTH:
         default:
            return this.makeShapeNorthSouth();
         case SOUTH:
            return this.makeShapeNorthSouth();
         case EAST:
         case WEST:
            return this.makeShapeEastWest();
         case DOWN:
         case UP:
            return this.makeShapeUpDown();
      }
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState pState) {
      return true;
   }

   @NotNull
   @Override
   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Direction direction = pState.getValue(FACING);
      switch (direction) {
         case NORTH:
         default:
            return this.makeShapeNorthSouth();
         case SOUTH:
            return this.makeShapeNorthSouth();
         case EAST:
         case WEST:
            return this.makeShapeEastWest();
         case DOWN:
         case UP:
            return this.makeShapeUpDown();
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(FACING);
   }
}
