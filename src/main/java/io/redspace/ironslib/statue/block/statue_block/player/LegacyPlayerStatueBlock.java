package io.redspace.ironslib.statue.block.statue_block.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class LegacyPlayerStatueBlock extends PlayerStatueBlock {
   @Nullable
   @Override
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new LegacyPlayerStatueBlockEntity(pos, state);
   }
}
