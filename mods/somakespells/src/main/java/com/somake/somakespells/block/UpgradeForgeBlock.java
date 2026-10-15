package com.somake.somakespells.block;

import com.mojang.serialization.MapCodec;
import com.somake.somakespells.block.entity.UpgradeForgeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpgradeForgeBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
   public static final MapCodec<UpgradeForgeBlock> CODEC = simpleCodec(properties -> new UpgradeForgeBlock());
   public static final EnumProperty<ChestType> PART = BlockStateProperties.CHEST_TYPE;
   private static final double[][] HITBOX_REFERENCE_BOXES = new double[][]{
      {0.0, 10.0, 0.0, 32.0, 14.0, 16.0},
      {2.5, 14.0, 13.5, 29.5, 28.1, 14.5},
      {27.0, 2.0, 1.0, 30.0, 10.0, 4.0},
      {27.0, 2.0, 12.0, 30.0, 10.0, 15.0},
      {2.0, 2.0, 12.0, 5.0, 10.0, 15.0},
      {2.0, 2.0, 1.0, 5.0, 10.0, 4.0},
      {26.7, 0.0, 0.7, 30.3, 2.0, 4.3},
      {26.7, 8.7, 0.7, 30.3, 10.7, 4.3},
      {1.7, 8.7, 0.7, 5.3, 10.7, 4.3},
      {1.7, 8.7, 11.7, 5.3, 10.7, 15.3},
      {26.7, 8.7, 11.7, 30.3, 10.7, 15.3},
      {1.7, 0.0, 0.7, 5.3, 2.0, 4.3},
      {1.7, 0.0, 11.7, 5.3, 2.0, 15.3},
      {26.7, 0.0, 11.7, 30.3, 2.0, 15.3}
   };
   private static final VoxelShape SHAPE_SOUTH_LEFT_HALF = buildHalfShape(0.0, 16.0);
   private static final VoxelShape SHAPE_SOUTH_RIGHT_HALF = buildHalfShape(16.0, 32.0);
   private static final VoxelShape SHAPE_WEST_LEFT_HALF = rotateClockwise(SHAPE_SOUTH_LEFT_HALF);
   private static final VoxelShape SHAPE_WEST_RIGHT_HALF = rotateClockwise(SHAPE_SOUTH_RIGHT_HALF);
   private static final VoxelShape SHAPE_NORTH_LEFT_HALF = rotateClockwise(SHAPE_WEST_LEFT_HALF);
   private static final VoxelShape SHAPE_NORTH_RIGHT_HALF = rotateClockwise(SHAPE_WEST_RIGHT_HALF);
   private static final VoxelShape SHAPE_EAST_LEFT_HALF = rotateClockwise(SHAPE_NORTH_LEFT_HALF);
   private static final VoxelShape SHAPE_EAST_RIGHT_HALF = rotateClockwise(SHAPE_NORTH_RIGHT_HALF);

   public UpgradeForgeBlock() {
      super(Properties.ofFullCopy(Blocks.ENCHANTING_TABLE).noOcclusion().sound(SoundType.NETHERITE_BLOCK));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(PART, ChestType.RIGHT))
            .setValue(BlockStateProperties.WATERLOGGED, false)
      );
   }

   @NotNull
   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      Direction facing = ((Direction)state.getValue(FACING)).getOpposite();
      ChestType part = (ChestType)state.getValue(PART);

      return switch (facing) {
         case SOUTH -> part == ChestType.RIGHT ? SHAPE_SOUTH_LEFT_HALF : SHAPE_SOUTH_RIGHT_HALF;
         case WEST -> part == ChestType.RIGHT ? SHAPE_WEST_LEFT_HALF : SHAPE_WEST_RIGHT_HALF;
         case NORTH -> part == ChestType.RIGHT ? SHAPE_NORTH_LEFT_HALF : SHAPE_NORTH_RIGHT_HALF;
         case EAST -> part == ChestType.RIGHT ? SHAPE_EAST_LEFT_HALF : SHAPE_EAST_RIGHT_HALF;
         default -> SHAPE_SOUTH_LEFT_HALF;
      };
   }

   private static VoxelShape buildHalfShape(double halfStartX, double halfEndX) {
      VoxelShape shape = Shapes.empty();

      for (double[] box : HITBOX_REFERENCE_BOXES) {
         double clippedMinX = Math.max(box[0], halfStartX);
         double clippedMaxX = Math.min(box[3], halfEndX);
         if (!(clippedMinX >= clippedMaxX)) {
            shape = Shapes.or(shape, Block.box(clippedMinX - halfStartX, box[1], box[2], clippedMaxX - halfStartX, box[4], box[5]));
         }
      }

      return shape;
   }

   private static VoxelShape rotateClockwise(VoxelShape shape) {
      VoxelShape rotated = Shapes.empty();

      for (AABB box : shape.toAabbs()) {
         double minX = box.minX * 16.0;
         double minY = box.minY * 16.0;
         double minZ = box.minZ * 16.0;
         double maxX = box.maxX * 16.0;
         double maxY = box.maxY * 16.0;
         double maxZ = box.maxZ * 16.0;
         rotated = Shapes.or(rotated, Block.box(16.0 - maxZ, minY, minX, 16.0 - minZ, maxY, maxX));
      }

      return rotated;
   }

   @NotNull
   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      ChestType half = (ChestType)state.getValue(PART);
      BlockPos requiredNeighborPos = pos.relative(getNeighbourDirection(half, (Direction)state.getValue(FACING)));
      boolean waterlogged = (Boolean)state.getValue(BlockStateProperties.WATERLOGGED);
      if (waterlogged) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
         level.scheduleTick(requiredNeighborPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      if (!level.getBlockState(requiredNeighborPos).is(this)) {
         BlockState air = (waterlogged ? Blocks.WATER : Blocks.AIR).defaultBlockState();
         level.setBlock(pos, air, 35);
         level.levelEvent(null, 2001, pos, Block.getId(air));
         return air;
      } else {
         return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
      }
   }

   private static Direction getNeighbourDirection(ChestType part, Direction facing) {
      return part == ChestType.LEFT ? facing.getCounterClockWise() : facing.getClockWise();
   }

   @NotNull
   protected FluidState getFluidState(BlockState state) {
      return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction direction = context.getHorizontalDirection();
      BlockPos blockPos = context.getClickedPos();
      BlockPos neighborPos = blockPos.relative(direction.getCounterClockWise());
      Level level = context.getLevel();
      return level.getBlockState(neighborPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(neighborPos)
         ? (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, direction.getOpposite())).setValue(PART, ChestType.RIGHT))
            .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(blockPos).getType() == Fluids.WATER)
         : null;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide) {
         BlockPos neighborPos = pos.relative(((Direction)state.getValue(FACING)).getClockWise());
         level.setBlock(
            neighborPos,
            (BlockState)((BlockState)state.setValue(PART, ChestType.LEFT))
               .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(neighborPos).getType() == Fluids.WATER),
            3
         );
         level.setBlock(pos, (BlockState)state.setValue(PART, ChestType.RIGHT), 3);
         level.blockUpdated(pos, Blocks.AIR);
         state.updateNeighbourShapes(level, pos, 3);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PART, BlockStateProperties.WATERLOGGED});
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos mainPos = getMainBlockPos(state, pos);
         MenuProvider menuProvider = this.getMenuProvider(state, level, mainPos);
         if (menuProvider == null) {
            return InteractionResult.CONSUME;
         } else {
            player.openMenu(menuProvider);
            return InteractionResult.CONSUME;
         }
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
      return level.getBlockEntity(pos) instanceof UpgradeForgeBlockEntity forgeBlockEntity ? forgeBlockEntity : null;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new UpgradeForgeBlockEntity(pos, state);
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (state.getBlock() != newState.getBlock()
         && !level.isClientSide
         && state.getValue(PART) == ChestType.RIGHT
         && level.getBlockEntity(pos) instanceof UpgradeForgeBlockEntity forgeBlockEntity) {
         ItemStack book = forgeBlockEntity.removeItemNoUpdate(0);
         if (!book.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), book);
         }
      }

      super.onRemove(state, level, pos, newState, movedByPiston);
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.BLOCK;
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return state.getValue(PART) == ChestType.RIGHT ? RenderShape.MODEL : RenderShape.INVISIBLE;
   }

   private static BlockPos getMainBlockPos(BlockState state, BlockPos pos) {
      return state.getValue(PART) == ChestType.RIGHT ? pos : pos.relative(getNeighbourDirection(ChestType.LEFT, (Direction)state.getValue(FACING)));
   }
}
