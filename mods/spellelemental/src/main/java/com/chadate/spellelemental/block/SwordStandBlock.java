package com.chadate.spellelemental.block;

import com.chadate.spellelemental.block.entity.SwordStandBlockEntity;
import com.chadate.spellelemental.config.ServerConfig;
import com.chadate.spellelemental.integration.jei.data.SwordOilConfigLoader;
import com.chadate.spellelemental.util.ActionBarMessageUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SwordStandBlock extends BaseEntityBlock {
   public static final MapCodec<SwordStandBlock> CODEC = simpleCodec(SwordStandBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape NORTH_SHAPE = Shapes.or(Block.box(1.0, 0.0, 4.0, 15.0, 1.0, 12.0), Block.box(2.0, 1.0, 5.0, 14.0, 2.0, 11.0));
   private static final VoxelShape SOUTH_SHAPE = Shapes.or(Block.box(1.0, 0.0, 4.0, 15.0, 1.0, 12.0), Block.box(2.0, 1.0, 5.0, 14.0, 2.0, 11.0));
   private static final VoxelShape EAST_SHAPE = Shapes.or(Block.box(4.0, 0.0, 1.0, 12.0, 1.0, 15.0), Block.box(5.0, 1.0, 2.0, 11.0, 2.0, 14.0));
   private static final VoxelShape WEST_SHAPE = Shapes.or(Block.box(4.0, 0.0, 1.0, 12.0, 1.0, 15.0), Block.box(5.0, 1.0, 2.0, 11.0, 2.0, 14.0));

   public SwordStandBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);

      return switch (facing) {
         case NORTH -> NORTH_SHAPE;
         case SOUTH -> SOUTH_SHAPE;
         case EAST -> EAST_SHAPE;
         case WEST -> WEST_SHAPE;
         default -> NORTH_SHAPE;
      };
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new SwordStandBlockEntity(pos, state);
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      @NotNull ItemStack stack,
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hitResult
   ) {
      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      } else if (level.getBlockEntity(pos) instanceof SwordStandBlockEntity swordStand) {
         SwordOilConfigLoader.SwordOilConfig swordOilConfig = SwordOilConfigLoader.getSwordOilConfig(stack.getItem());
         if (swordOilConfig != null && !swordStand.isEmpty()) {
            ItemStack sword = swordStand.getSword();
            if (this.applySwordOil(sword, swordOilConfig, player)) {
               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               swordStand.setSword(sword);
               level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.2F);
               return ItemInteractionResult.SUCCESS;
            } else {
               return ItemInteractionResult.FAIL;
            }
         } else if (this.isSwordItem(stack) && swordStand.isEmpty()) {
            ItemStack swordToPlace = stack.copy();
            swordToPlace.setCount(1);
            swordStand.setSword(swordToPlace);
            if (!player.isCreative()) {
               stack.shrink(1);
            }

            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else {
         return ItemInteractionResult.FAIL;
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult
   ) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else if (level.getBlockEntity(pos) instanceof SwordStandBlockEntity swordStand) {
         if (!swordStand.isEmpty()) {
            ItemStack removedSword = swordStand.removeItem();
            if (player.addItem(removedSword)) {
               level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
               return InteractionResult.SUCCESS;
            }

            swordStand.setSword(removedSword);
         }

         return InteractionResult.PASS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   private boolean isSwordItem(ItemStack stack) {
      return stack.getItem() instanceof SwordItem;
   }

   private boolean applySwordOil(ItemStack sword, SwordOilConfigLoader.SwordOilConfig swordOilConfig, Player player) {
      if (!sword.isEmpty() && sword.getItem() instanceof SwordItem) {
         String elementType = swordOilConfig.getElement();
         int elementAmount = swordOilConfig.getAmount();
         int MAX_ELEMENT_AMOUNT = ServerConfig.getWeaponMaxElementAmount();
         CompoundTag tag = ((CustomData)sword.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         CompoundTag elementData = tag.getCompound("ElementAttachment");
         if (elementData.contains("element")) {
            String currentElement = elementData.getString("element");
            if (!currentElement.equals(elementType)) {
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(
                     serverPlayer,
                     "message.spellelemental.sword_oil.different_element",
                     Component.translatable("element.spellelemental." + currentElement),
                     Component.translatable("element.spellelemental." + elementType)
                  );
               }

               return false;
            }

            int currentAmount = elementData.getInt("amount");
            int newAmount = Math.min(currentAmount + elementAmount, MAX_ELEMENT_AMOUNT);
            if (newAmount == currentAmount) {
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(
                     serverPlayer,
                     "message.spellelemental.sword_oil.limit_reached",
                     Component.translatable("element.spellelemental." + elementType),
                     MAX_ELEMENT_AMOUNT
                  );
               }

               return false;
            }

            if (newAmount < currentAmount + elementAmount) {
               elementData.putInt("amount", newAmount);
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(
                     serverPlayer,
                     "message.spellelemental.sword_oil.partial_extended",
                     Component.translatable("element.spellelemental." + elementType),
                     newAmount,
                     MAX_ELEMENT_AMOUNT
                  );
               }
            } else {
               elementData.putInt("amount", newAmount);
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(
                     serverPlayer, "message.spellelemental.sword_oil.extended", Component.translatable("element.spellelemental." + elementType)
                  );
               }
            }
         } else {
            int finalAmount = Math.min(elementAmount, MAX_ELEMENT_AMOUNT);
            elementData.putString("element", elementType);
            elementData.putInt("amount", finalAmount);
            elementData.putLong("appliedTime", System.currentTimeMillis());
            tag.put("ElementAttachment", elementData);
            if (finalAmount < elementAmount) {
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(
                     serverPlayer,
                     "message.spellelemental.sword_oil.applied_limited",
                     Component.translatable("element.spellelemental." + elementType),
                     finalAmount,
                     MAX_ELEMENT_AMOUNT
                  );
               }
            } else if (player instanceof ServerPlayer serverPlayer) {
               ActionBarMessageUtil.sendTranslatableActionBarMessage(
                  serverPlayer, "message.spellelemental.sword_oil.applied", Component.translatable("element.spellelemental." + elementType)
               );
            }
         }

         sword.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         return true;
      } else {
         return false;
      }
   }

   public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SwordStandBlockEntity swordStand && !swordStand.isEmpty()) {
         ItemStack drop = swordStand.removeItem();
         if (!drop.isEmpty()) {
            popResource(level, pos, drop);
         }
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @NotNull
   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }
}
