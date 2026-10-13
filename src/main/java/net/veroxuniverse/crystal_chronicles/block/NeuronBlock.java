package net.veroxuniverse.crystal_chronicles.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
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

public class NeuronBlock extends HalfTransparentBlock {
   public static final VoxelShape SHAPE_BASE = Block.box(3.0, 3.0, 3.0, 13.0, 13.0, 13.0);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   private static final int MIN_TICK_INTERVAL = 100;
   private static final int MAX_TICK_INTERVAL = 100;
   private static final int MIN_LIGHT_DURATION = 100;
   private static final int MAX_LIGHT_DURATION = 100;
   private static final int MAX_AXONS = 10;

   public NeuronBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   @Override
   protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      if (!pLevel.isClientSide) {
         pLevel.scheduleTick(pPos, this, this.getRandomInterval(100, 100));
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return this.defaultBlockState().setValue(LIT, Boolean.valueOf(false));
   }

   @Override
   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      int tickInterval = 100 + pRandom.nextInt(1);
      int lightDuration = 100 + pRandom.nextInt(1);
      boolean shouldBeLit = !pState.getValue(LIT);
      pLevel.setBlock(pPos, pState.setValue(LIT, Boolean.valueOf(shouldBeLit)), 3);
      this.updateAboveBlocks(pLevel, pPos);
      pLevel.scheduleTick(pPos, this, tickInterval);
   }

   private void updateAboveBlocks(ServerLevel pLevel, BlockPos pPos) {
      BlockPos currentPos = pPos.above();

      for (int count = 0; currentPos != null && count < 10; currentPos = currentPos.above()) {
         BlockState currentState = pLevel.getBlockState(currentPos);
         if (!(currentState.getBlock() instanceof AxonBlock)) {
            break;
         }

         boolean shouldBeLit = pLevel.getBlockState(pPos).getValue(LIT);
         if (currentState.getValue(AxonBlock.LIT) != shouldBeLit) {
            pLevel.setBlock(currentPos, currentState.setValue(AxonBlock.LIT, Boolean.valueOf(shouldBeLit)), 3);
            count++;
         }
      }
   }

   @Override
   public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
      return state.getLightEmission();
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

   private int getRandomInterval(int minTicks, int maxTicks) {
      Random random = new Random();
      return minTicks + random.nextInt(maxTicks - minTicks + 1);
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
