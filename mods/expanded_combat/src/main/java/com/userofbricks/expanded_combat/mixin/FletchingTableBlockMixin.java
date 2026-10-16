package com.userofbricks.expanded_combat.mixin;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.registry.ApiHelper;
import com.userofbricks.expanded_combat.inventory.container.FletchingTableMenu;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mixin({FletchingTableBlock.class})
public class FletchingTableBlockMixin extends CraftingTableBlock {
   public FletchingTableBlockMixin(Properties p_i49985_1_) {
      super(p_i49985_1_);
   }

   public MenuProvider getMenuProvider(BlockState blockState, Level world, BlockPos pos) {
      return new SimpleMenuProvider(
         (i, playerInventory, playerEntity) -> new FletchingTableMenu(i, playerInventory, ContainerLevelAccess.create(world, pos)), Component.empty()
      );
   }

   @Inject(
      method = {"useWithoutItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void use(
      BlockState blockState, Level world, BlockPos pos, Player playerEntity, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir
   ) {
      if (ApiHelper.doesAnyPluginDenyFletchingTableGui(ExpandedCombat.PLUGINS) || !ExpandedCombat.CONFIG.enableFletchingTable) {
         cir.setReturnValue(InteractionResult.PASS);
      } else if (world.isClientSide) {
         cir.setReturnValue(InteractionResult.SUCCESS);
      } else {
         playerEntity.openMenu(blockState.getMenuProvider(world, pos));
         cir.setReturnValue(InteractionResult.CONSUME);
      }
   }
}
