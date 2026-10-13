package net.veroxuniverse.crystal_chronicles.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class AxonBlock extends HalfTransparentBlock {
   public static final VoxelShape SHAPE_BASE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;

   public AxonBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)));
   }

   @Override
   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      if (!pLevel.isClientSide) {
         this.updateBlocks(pLevel, pPos);
      }
   }

   @Override
   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      this.updateBlocks(pLevel, pPos);
      pLevel.scheduleTick(pPos, this, 10);
   }

   private void updateBlocks(Level pLevel, BlockPos pPos) {
      BlockPos blockBelow = pPos.below();
      BlockState belowState = pLevel.getBlockState(blockBelow);
      if (belowState.getBlock() instanceof NeuronBlock) {
         boolean shouldBeLit = belowState.getValue(NeuronBlock.LIT);
         BlockState currentState = pLevel.getBlockState(pPos);
         if (currentState.getValue(LIT) != shouldBeLit) {
            pLevel.setBlock(pPos, currentState.setValue(LIT, Boolean.valueOf(shouldBeLit)), 3);
         }
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(LIT);
   }

   @Override
   public boolean isSignalSource(BlockState state) {
      return false;
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.NORMAL;
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
}
