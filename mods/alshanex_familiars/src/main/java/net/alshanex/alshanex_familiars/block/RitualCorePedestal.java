package net.alshanex.alshanex_familiars.block;

import io.redspace.ironsspellbooks.block.pedestal.PedestalBlock;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.block.entity.RitualCorePedestalEntity;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RitualCorePedestal extends PedestalBlock {
   private static final VoxelShape LEG1 = Block.box(0.0, 0.0, 6.0, 4.0, 4.0, 10.0);
   private static final VoxelShape LEG2 = Block.box(6.0, 0.0, 12.0, 10.0, 4.0, 16.0);
   private static final VoxelShape LEG3 = Block.box(12.0, 0.0, 6.0, 16.0, 4.0, 10.0);
   private static final VoxelShape LEG4 = Block.box(6.0, 0.0, 0.0, 10.0, 4.0, 4.0);
   private static final VoxelShape SECOND_LEG1 = Block.box(5.5, 0.0, 11.0, 10.5, 5.0, 16.0);
   private static final VoxelShape SECOND_LEG2 = Block.box(5.5, 0.0, 0.0, 10.5, 5.0, 5.0);
   private static final VoxelShape SECOND_LEG3 = Block.box(11.0, 0.0, 5.5, 16.0, 5.0, 10.5);
   private static final VoxelShape SECOND_LEG4 = Block.box(0.0, 0.0, 5.5, 5.0, 5.0, 10.5);
   private static final VoxelShape CENTER = Block.box(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);
   private static final VoxelShape TOP = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE = Shapes.or(LEG1, new VoxelShape[]{LEG2, LEG3, LEG4, SECOND_LEG1, SECOND_LEG2, SECOND_LEG3, SECOND_LEG4, CENTER, TOP});

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock()) {
         BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
         if (blockEntity instanceof RitualCorePedestalEntity) {
            ((RitualCorePedestalEntity)blockEntity).drops();
         }
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new RitualCorePedestalEntity(pos, state);
   }

   public ItemInteractionResult useItemOn(
      ItemStack pStack, BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!pLevel.isClientSide() && pLevel.getBlockEntity(pos) instanceof RitualCorePedestalEntity pedestalTile) {
         ItemStack currentPedestalItem = pedestalTile.getHeldItem();
         ItemStack handItem = player.getItemInHand(hand);
         ItemStack playerItem = currentPedestalItem.copy();
         if (!handItem.isEmpty() && handItem.getCount() != 1) {
            this.dropItem(playerItem, player);
         } else {
            player.setItemInHand(hand, playerItem);
         }

         pedestalTile.setHeldItem(ItemStack.EMPTY);
         currentPedestalItem = handItem.copy();
         if (!currentPedestalItem.isEmpty()) {
            currentPedestalItem.setCount(1);
            pedestalTile.setHeldItem(currentPedestalItem);
            handItem.shrink(1);
         }

         pLevel.sendBlockUpdated(pos, state, state, 2);
      }

      return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
   }

   private void dropItem(ItemStack itemstack, Player owner) {
      if (owner instanceof ServerPlayer serverplayer) {
         ItemEntity itementity = serverplayer.drop(itemstack, false);
         if (itementity != null) {
            itementity.setNoPickUpDelay();
            itementity.setThrower(owner);
         }
      }
   }

   protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
      return 0.0F;
   }

   public float defaultDestroyTime() {
      return Float.MAX_VALUE;
   }

   public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
      return false;
   }

   @org.jetbrains.annotations.Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide()
         ? null
         : createTickerHelper(
            blockEntityType, BlockEntityRegistry.RITUAL_CORE.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
         );
   }
}
