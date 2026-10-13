package net.veroxuniverse.crystal_chronicles.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HolyMarblePillarBlock extends Block {
   public static final VoxelShape SHAPE_BASE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
   public static final EnumProperty<HolyMarblePillarBlock.Part> PART = EnumProperty.create("part", HolyMarblePillarBlock.Part.class);

   public HolyMarblePillarBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(PART, HolyMarblePillarBlock.Part.SINGLE));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(PART);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      HolyMarblePillarBlock.Part part = this.getPartFor(level, pos);
      return this.defaultBlockState().setValue(PART, part);
   }

   @Override
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @NotNull
   @Override
   public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return SHAPE_BASE;
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState pState) {
      return true;
   }

   @NotNull
   @Override
   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE_BASE;
   }

   @NotNull
   @Override
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
      return SHAPE_BASE;
   }

   @Override
   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      BlockState newState = state;
      if (direction == Direction.UP || direction == Direction.DOWN) {
         HolyMarblePillarBlock.Part newPart = this.getPartFor(level, pos);
         newState = state.setValue(PART, newPart);
      }

      return super.updateShape(newState, direction, neighborState, level, pos, neighborPos);
   }

   private HolyMarblePillarBlock.Part getPartFor(LevelAccessor level, BlockPos pos) {
      boolean upSame = level.getBlockState(pos.above()).is(this);
      boolean downSame = level.getBlockState(pos.below()).is(this);
      if (upSame && downSame) {
         return HolyMarblePillarBlock.Part.MIDDLE;
      } else if (upSame) {
         return HolyMarblePillarBlock.Part.BOTTOM;
      } else {
         return downSame ? HolyMarblePillarBlock.Part.TOP : HolyMarblePillarBlock.Part.SINGLE;
      }
   }

   public static enum Part implements StringRepresentable {
      SINGLE("single"),
      TOP("top"),
      MIDDLE("middle"),
      BOTTOM("bottom");

      private final String name;

      private Part(String name) {
         this.name = name;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
