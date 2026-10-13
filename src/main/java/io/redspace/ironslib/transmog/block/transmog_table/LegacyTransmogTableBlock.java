package io.redspace.ironslib.transmog.block.transmog_table;

import io.redspace.ironslib.registry.IronsLibRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class LegacyTransmogTableBlock extends TransmogTableBlock {
   public LegacyTransmogTableBlock(Properties properties) {
      super(properties);
   }

   @NotNull
   @Override
   protected InteractionResult useWithoutItem(
      @NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHitResult
   ) {
      if (pLevel.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         pLevel.setBlock(pPos, ((Block)IronsLibRegistries.BlockRegistry.TRANSMOG_TABLE.get()).withPropertiesOf(pState), 4);
         return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
      }
   }
}
