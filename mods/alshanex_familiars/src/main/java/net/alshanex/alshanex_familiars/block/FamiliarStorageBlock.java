package net.alshanex.alshanex_familiars.block;

import com.mojang.serialization.MapCodec;
import net.alshanex.alshanex_familiars.block.entity.FamiliarStorageBlockEntity;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.familiarslib.block.AbstractFamiliarStorageBlock;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FamiliarStorageBlock extends AbstractFamiliarStorageBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   private static final VoxelShape SHAPE_LOWER = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_UPPER = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final MapCodec<FamiliarStorageBlock> CODEC = simpleCodec(FamiliarStorageBlock::new);

   public FamiliarStorageBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   public MapCodec<FamiliarStorageBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF, FACING});
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   private VoxelShape getShapeForState(BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_UPPER : SHAPE_LOWER;
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return this.getShapeForState(state);
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      return pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection())).setValue(HALF, DoubleBlockHalf.LOWER)
         : null;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      level.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
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

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FamiliarStorageBlockEntity(pos, state);
   }

   protected int getAmountPerChunk() {
      return 2;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide
         ? null
         : createTickerHelper(blockEntityType, BlockEntityRegistry.FAMILIAR_STORAGE.get(), AbstractFamiliarStorageBlockEntity::serverTick);
   }
}
