package net.alshanex.alshanex_familiars.block;

import com.mojang.serialization.MapCodec;
import io.redspace.ironsspellbooks.block.pedestal.PedestalBlock;
import java.util.List;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.block.entity.ArchmageStatueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArchmageStatue extends PedestalBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape SHAPE_CORE = Block.box(3.5, 1.51903, 5.22022, 12.5, 10.51903, 14.22022);
   private static final VoxelShape SHAPE_BODY = Block.box(3.0, 4.01903, 4.72022, 13.0, 11.01903, 14.72022);
   private static final VoxelShape SHAPE_BOTTOM_WEST = Block.box(2.31994, -4.10537, 4.72022, 3.0, 1.89463, 14.72022);
   private static final VoxelShape SHAPE_BOTTOM_EAST = Block.box(13.0, -4.10537, 4.72022, 13.68006, 1.89463, 14.72022);
   private static final VoxelShape SHAPE_BOTTOM_SOUTH = Block.box(3.0, -4.10537, 14.72022, 13.0, 1.89463, 15.40028);
   private static final VoxelShape SHAPE_BOTTOM_NORTH = Block.box(3.0, -4.10537, 4.04016, 13.0, 1.89463, 4.72022);
   private static final VoxelShape SHAPE_NORTH = Shapes.or(
      SHAPE_CORE, new VoxelShape[]{SHAPE_BODY, SHAPE_BOTTOM_WEST, SHAPE_BOTTOM_EAST, SHAPE_BOTTOM_SOUTH, SHAPE_BOTTOM_NORTH}
   );
   public static final MapCodec<ArchmageStatue> CODEC = simpleCodec(t -> new ArchmageStatue());

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

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public ArchmageStatue() {
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(BlockStateProperties.WATERLOGGED, false)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
      builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      switch ((Direction)state.getValue(FACING)) {
         case SOUTH:
            return rotateBox(SHAPE_NORTH, Rotation.CLOCKWISE_180);
         case EAST:
            return rotateBox(SHAPE_NORTH, Rotation.CLOCKWISE_90);
         case WEST:
            return rotateBox(SHAPE_NORTH, Rotation.COUNTERCLOCKWISE_90);
         default:
            return SHAPE_NORTH;
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock()) {
         BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
         if (blockEntity instanceof ArchmageStatueEntity) {
            ((ArchmageStatueEntity)blockEntity).drops();
         }
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ArchmageStatueEntity(pos, state);
   }

   public ItemInteractionResult useItemOn(
      ItemStack pStack, BlockState state, Level pLevel, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!pLevel.isClientSide() && pLevel.getBlockEntity(pos) instanceof ArchmageStatueEntity pedestalTile) {
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
}
