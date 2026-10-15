package net.hazen.hazennstuff.Item.Block.Statues.HazelStatue;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HazelStatue extends HorizontalDirectionalBlock implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape SHAPE_CORE = Block.box(4.0, 0.0, 4.0, 4.0, 64.0, 4.0);
   private static final VoxelShape SHAPE_BODY = Block.box(4.0, 0.0, 4.0, 4.0, 64.0, 4.0);
   private static final VoxelShape SHAPE_BOTTOM_WEST = Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
   private static final VoxelShape SHAPE_BOTTOM_EAST = Block.box(4.0, 0.0, 4.0, 4.0, 64.0, 4.0);
   private static final VoxelShape SHAPE_BOTTOM_SOUTH = Block.box(4.0, 0.0, 4.0, 4.0, 64.0, 4.0);
   private static final VoxelShape SHAPE_BOTTOM_NORTH = Block.box(4.0, 0.0, 4.0, 4.0, 64.0, 4.0);
   private static final VoxelShape SHAPE_NORTH = Shapes.or(
      SHAPE_CORE, new VoxelShape[]{SHAPE_BODY, SHAPE_BOTTOM_WEST, SHAPE_BOTTOM_EAST, SHAPE_BOTTOM_SOUTH, SHAPE_BOTTOM_NORTH}
   );

   private static VoxelShape rotateBox(VoxelShape shape, Rotation rotation) {
      List<AABB> boxes = shape.toAabbs();
      VoxelShape rotatedShape = Shapes.empty();

      for (AABB box : boxes) {
         double x1 = box.minX;
         double y1 = box.minY;
         double z1 = box.minZ;
         double x2 = box.maxX;
         double y2 = box.maxY;
         double z2 = box.maxZ;

         rotatedShape = Shapes.or(rotatedShape, switch (rotation) {
            case CLOCKWISE_90 -> Shapes.box(1.0 - z2, y1, x1, 1.0 - z1, y2, x2);
            case CLOCKWISE_180 -> Shapes.box(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1);
            case COUNTERCLOCKWISE_90 -> Shapes.box(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1);
            default -> Shapes.box(x1, y1, z1, x2, y2, z2);
         });
      }

      return rotatedShape;
   }

   public HazelStatue(Properties properties) {
      super(properties);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
      builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      switch ((Direction)state.getValue(FACING)) {
         case SOUTH:
            return rotateBox(SHAPE_NORTH, Rotation.CLOCKWISE_180);
         case EAST:
            return rotateBox(SHAPE_NORTH, Rotation.CLOCKWISE_90);
         case WEST:
            return rotateBox(SHAPE_NORTH, Rotation.COUNTERCLOCKWISE_90);
         default:
            return SHAPE_NORTH;
      }
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return null;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new HazelStatueBlockEntity(pos, state);
   }
}
