package net.veroxuniverse.crystal_chronicles.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SkinLayerBlock extends Block {
   public static final VoxelShape SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

   public SkinLayerBlock(Properties pProperties) {
      super(pProperties);
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
   public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
      return !levelReader.isEmptyBlock(pos.below());
   }

   @Override
   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
      super.onPlace(state, level, pos, oldState, isMoving);
      if (!level.isClientSide) {
         this.updateBelowSkinState(level, pos, true);
      }
   }

   @Override
   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      super.neighborChanged(state, level, pos, block, fromPos, isMoving);
      if (!level.isClientSide) {
         if (!this.canSurvive(state, level, pos)) {
            this.updateBelowSkinState(level, pos, false);
            level.destroyBlock(pos, false);
            return;
         }

         if (fromPos.equals(pos.below())) {
            this.updateBelowSkinState(level, pos, true);
         }
      }
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!level.isClientSide && !state.is(newState.getBlock())) {
         this.updateBelowSkinState(level, pos, false);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   private void updateBelowSkinState(Level level, BlockPos pos, boolean value) {
      BlockPos belowPos = pos.below();
      BlockState belowState = level.getBlockState(belowPos);
      if (belowState.getBlock() instanceof FleshBlock
         && belowState.hasProperty(FleshBlock.HAS_SKIN_ABOVE)
         && belowState.getValue(FleshBlock.HAS_SKIN_ABOVE) != value) {
         level.setBlock(belowPos, belowState.setValue(FleshBlock.HAS_SKIN_ABOVE, Boolean.valueOf(value)), 2);
      }
   }
}
