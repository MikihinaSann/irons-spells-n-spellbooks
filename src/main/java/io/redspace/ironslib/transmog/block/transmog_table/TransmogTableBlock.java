package io.redspace.ironslib.transmog.block.transmog_table;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TransmogTableBlock extends Block {
   public TransmogTableBlock(Properties properties) {
      super(properties);
   }

   public PushReaction getPistonPushReaction(@NotNull BlockState pState) {
      return PushReaction.BLOCK;
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHitResult
   ) {
      if (pLevel.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         io.redspace.ironsspellbooks.platform.fabric.FabricMenuHelper.openMenu((net.minecraft.server.level.ServerPlayer) pPlayer, pState.getMenuProvider(pLevel, pPos));
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
      return new SimpleMenuProvider(
         (i, inventory, player) -> new TransmogTableMenu(i, inventory, ContainerLevelAccess.create(pLevel, pPos)),
         Component.translatable("block.irons_lib.inscription_table")
      );
   }
}
