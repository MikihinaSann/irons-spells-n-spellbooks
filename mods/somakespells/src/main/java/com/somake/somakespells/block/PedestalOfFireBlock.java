package com.somake.somakespells.block;

import com.mojang.serialization.MapCodec;
import com.somake.somakespells.block.entity.PedestalOfFireBlockEntity;
import com.somake.somakespells.event.PedestalRitualHandler;
import com.somake.somakespells.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PedestalOfFireBlock extends BaseEntityBlock {
   public static final MapCodec<PedestalOfFireBlock> CODEC = simpleCodec(properties -> new PedestalOfFireBlock());
   private static final VoxelShape SHAPE = Shapes.or(Block.box(5.4, 0.0, 5.4, 10.6, 8.0, 10.6), Block.box(4.0, 8.0, 4.0, 12.0, 13.0, 12.0));

   public PedestalOfFireBlock() {
      super(Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion().strength(4.0F, 1200.0F));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.BLOCK;
   }

   public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.getBlockEntity(pos) instanceof PedestalOfFireBlockEntity pedestal) {
         if (pedestal.isRitualActive()) {
            return ItemInteractionResult.SUCCESS;
         } else {
            ItemStack heldItem = player.getItemInHand(hand);
            if (!player.isShiftKeyDown() && !heldItem.isEmpty()) {
               if (!level.isClientSide) {
                  ItemStack copy = heldItem.copy();
                  copy.setCount(1);
                  if (pedestal.addItem(copy) && !player.getAbilities().instabuild) {
                     heldItem.shrink(1);
                  }
               }

               return ItemInteractionResult.SUCCESS;
            } else {
               if (!level.isClientSide) {
                  ItemStack removed = pedestal.removeLastItem();
                  if (!removed.isEmpty()) {
                     Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, removed);
                  }
               }

               return ItemInteractionResult.SUCCESS;
            }
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PedestalOfFireBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return createTickerHelper(blockEntityType, (BlockEntityType)ModBlockEntities.PEDESTAL_OF_FIRE.get(), PedestalOfFireBlockEntity::commonTick);
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (state.getBlock() != newState.getBlock() && !level.isClientSide && level.getBlockEntity(pos) instanceof PedestalOfFireBlockEntity pedestal) {
         if (level instanceof ServerLevel serverLevel && pedestal.isEmpoweredRitualActive() && pedestal.getRitualAltarPos() != null) {
            PedestalRitualHandler.resetSoulFireNetwork(serverLevel, pedestal.getRitualAltarPos());
         }

         pedestal.dropAll(level, pos);
      }

      super.onRemove(state, level, pos, newState, movedByPiston);
   }
}
