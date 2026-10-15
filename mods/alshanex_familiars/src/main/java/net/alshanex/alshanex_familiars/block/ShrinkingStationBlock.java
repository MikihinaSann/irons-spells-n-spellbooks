package net.alshanex.alshanex_familiars.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.alshanex.alshanex_familiars.block.entity.ShrinkingStationBlockEntity;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ShrinkingStationBlock extends BaseEntityBlock {
   public static final MapCodec<ShrinkingStationBlock> CODEC = simpleCodec(ShrinkingStationBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final VoxelShape SHAPE_LOWER_NORTH = Shapes.or(Block.box(0.0, 6.0, 0.0, 16.0, 16.0, 16.0), Block.box(1.0, 0.0, 1.0, 15.0, 6.0, 15.0));
   public static final VoxelShape SHAPE_UPPER_NORTH = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 2.0, 11.0), Block.box(3.5, 2.0, 3.5, 12.5, 11.0, 12.5));

   public ShrinkingStationBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(BlockStateProperties.WATERLOGGED, false))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new ShrinkingStationBlockEntity(pos, state) : null;
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (state.getValue(HALF) == DoubleBlockHalf.LOWER && level.getBlockEntity(pos) instanceof ShrinkingStationBlockEntity shrinkingStationBlockEntity) {
            shrinkingStationBlockEntity.drops();
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf half = (DoubleBlockHalf)state.getValue(HALF);
      if (facing.getAxis() == Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
         return facingState.is(this) && facingState.getValue(HALF) != half
            ? (BlockState)state.setValue(FACING, (Direction)facingState.getValue(FACING))
            : Blocks.AIR.defaultBlockState();
      } else {
         return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
      }
   }

   public ItemInteractionResult useItemOn(
      ItemStack pStack, BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockPos lowerPos = pos.below();
         BlockState lowerState = pLevel.getBlockState(lowerPos);
         return lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER
            ? this.useItemOn(pStack, lowerState, pLevel, lowerPos, player, hand, hit)
            : ItemInteractionResult.FAIL;
      } else {
         if (!pLevel.isClientSide() && pLevel.getBlockEntity(pos) instanceof ShrinkingStationBlockEntity shrinkingStationBlockEntity) {
            ItemStack handItem = player.getItemInHand(hand);
            if (!handItem.isEmpty()) {
               ItemStack inputItem = shrinkingStationBlockEntity.getInputItem();
               ItemStack outputItem = shrinkingStationBlockEntity.getOutputItem();
               if (inputItem.isEmpty() && outputItem.isEmpty()) {
                  ItemStack toInsert = handItem.copy();
                  toInsert.setCount(1);
                  shrinkingStationBlockEntity.setInputItem(toInsert);
                  handItem.shrink(1);
                  pLevel.sendBlockUpdated(pos, state, state, 2);
                  return ItemInteractionResult.SUCCESS;
               }
            } else {
               ItemStack outputItem = shrinkingStationBlockEntity.getOutputItem();
               ItemStack inputItem = shrinkingStationBlockEntity.getInputItem();
               if (!outputItem.isEmpty()) {
                  player.setItemInHand(hand, outputItem.copy());
                  shrinkingStationBlockEntity.setOutputItem(ItemStack.EMPTY);
                  pLevel.sendBlockUpdated(pos, state, state, 2);
                  return ItemInteractionResult.SUCCESS;
               }

               if (!inputItem.isEmpty()) {
                  player.setItemInHand(hand, inputItem.copy());
                  shrinkingStationBlockEntity.setInputItem(ItemStack.EMPTY);
                  pLevel.sendBlockUpdated(pos, state, state, 2);
                  return ItemInteractionResult.SUCCESS;
               }
            }
         }

         return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return !level.isClientSide() && state.getValue(HALF) != DoubleBlockHalf.UPPER
         ? createTickerHelper(
            blockEntityType,
            BlockEntityRegistry.SHRINKING_STATION_BE.get(),
            (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState)
         )
         : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, BlockStateProperties.WATERLOGGED, HALF});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      return pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)
         ? (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
               .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER))
            .setValue(HALF, DoubleBlockHalf.LOWER)
         : null;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      level.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      if (!level.isClientSide && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockPos blockpos = pos.below();
         BlockState blockstate = level.getBlockState(blockpos);
         if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
            level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
         }
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      VoxelShape baseShape = state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_LOWER_NORTH : SHAPE_UPPER_NORTH;
      switch ((Direction)state.getValue(FACING)) {
         case SOUTH:
            return rotateBox(baseShape, Rotation.CLOCKWISE_180);
         case EAST:
            return rotateBox(baseShape, Rotation.CLOCKWISE_90);
         case WEST:
            return rotateBox(baseShape, Rotation.COUNTERCLOCKWISE_90);
         default:
            return baseShape;
      }
   }

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
}
